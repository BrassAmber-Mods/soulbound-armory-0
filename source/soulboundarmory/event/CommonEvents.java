package soulboundarmory.event;

import java.lang.management.ManagementFactory;
import net.auoeke.reflect.Accessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.command.SoulboundArmoryCommand;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.item.tool.ToolComponent;
import soulboundarmory.component.soulbound.item.weapon.WeaponComponent;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.config.Configuration;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.registry.Skills;

@EventBusSubscriber(modid = SoulboundArmory.ID)
public final class CommonEvents {
    /**
     Remove soulbound items that are to be preserved from drops and give them to the player.
     Insert dropped items into the attacker's inventory if the attacker has {@link Skills#enderPull}.
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void playerDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof PlayerEntity player) {
            if (!player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
                Components.soulbound(player)
                    .map(SoulboundComponent::item)
                    .filter(component -> component != null && component.intValue(StatisticType.level) >= Configuration.instance().preservationLevel)
                    .forEach(component -> event.getDrops().removeIf(drop -> component.accepts(drop.getStack()) && player.giveItemStack(drop.getStack())));
            }
        } else {
            ItemComponent.fromAttacker(event.getEntityLiving(), event.getSource()).ifPresent(component -> {
                if (component.hasSkill(Skills.enderPull)) {
                    event.getDrops().forEach(drop -> component.player.getInventory().insertStack(drop.getStack()));
                }
            });
        }
    }

    /**
     Apply {@link StatisticType#efficiency}.
     */
    @SubscribeEvent
    public static void breakSpeed(PlayerEvent.BreakSpeed event) {
        ItemComponent.fromMainHand(event.getPlayer()).ifPresent(item -> {
            var efficiency = item.floatValue(StatisticType.efficiency);

            if (item instanceof ToolComponent) {
                if (!item.stack().isSuitableFor(event.getState())) {
                    return;
                }
            } else if (efficiency == 0) {
                event.setNewSpeed(0);

                return;
            }

            event.setNewSpeed(event.getNewSpeed() + efficiency);
        });
    }

    /**
     Give the player experience directly if {@link Skills#enderPull} is unlocked.
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void breakBlock(BlockEvent.BreakEvent event) {
        ItemComponent.fromMainHand(event.getPlayer()).ifPresent(component -> {
            if (component.hasSkill(Skills.enderPull)) {
                component.player.addExperience(event.getExpToDrop());
                event.setExpToDrop(0);
            }
        });
    }

    /**
     Determine whether player hits are critical and apply {@link Skills#nourishment}.
     */
    @SubscribeEvent
    public static void damage(LivingDamageEvent event) {
        var damage = event.getSource();
        var target = event.getEntityLiving();

        if (damage.getAttacker() instanceof ServerPlayerEntity player) {
            ItemComponent.fromAttacker(target, damage).ifPresent(component -> {
                var amount = event.getAmount();

                if (((WeaponComponent<?>) component).hit()) {
                    event.setAmount(amount *= 2);
                    Packets.clientCriticalHitParticles.sendNearby(target, new ExtendedPacketBuffer().writeEntity(target));
                }

                if (component.hasSkill(Skills.nourishment)) {
                    var value = (1 + component.skill(Skills.nourishment).level()) * amount / 20F;
                    player.getHungerManager().add((int) (value + 1) / 2, 1.5F * value);
                }
            });
        }
    }

    /**
     Cancel damage to leaping players.
     */
    @SubscribeEvent
    public static void livingAttack(LivingAttackEvent event) {
        var target = event.getEntityLiving();
        var damage = event.getSource();

        if (target instanceof ServerPlayerEntity && ItemComponentType.greatsword.of(target).leapForce() > 0 && damage.getAttacker() != null && !damage.isExplosive() && !damage.isProjectile()) {
            event.setCanceled(true);
        }
    }

    /**
     Cancel knockback to leaping players.
     */
    @SubscribeEvent
    public static void knockback(LivingKnockBackEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity player) {
            if (ItemComponentType.greatsword.of(player).leapForce() > 0) {
                event.setCanceled(true);
            }
        }
    }

    /**
     Add experience points for kills.
     */
    @SubscribeEvent
    public static void death(LivingDeathEvent event) {
        ItemComponent.fromAttacker(event.getEntityLiving(), event.getSource()).ifPresent(component -> component.killed(event.getEntityLiving()));
    }

    /**
     Reduce fall damage, freeze nearby entities and add circular snow particles after landing from a leap.
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void fall(LivingFallEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity player) {
            var greatsword = ItemComponentType.greatsword.of(player);
            var leapForce = greatsword.leapForce();

            if (leapForce > 0) {
                var distance = event.getDistance() - (greatsword.zenith() + 3 + 3 * leapForce);

                if (distance <= 0) {
                    event.setCanceled(true);
                } else {
                    event.setDistance(distance * Math.min(1, 1.4F - leapForce));
                }

                greatsword.leapDuration = 4;

                if (greatsword.hasSkill(Skills.freezing)) {
                    var radiusXZ = Math.min(4, event.getDistance());
                    var radiusY = Math.min(2, 0.5F * event.getDistance());
                    var nearbyEntities = player.world.getOtherEntities(player, new Box(
                        player.getX() - radiusXZ, player.getY() - radiusY, player.getZ() - radiusXZ,
                        player.getX() + radiusXZ, player.getY() + radiusY, player.getZ() + radiusXZ
                    ));

                    var froze = false;

                    for (var entity : nearbyEntities) {
                        if (entity.squaredDistanceTo(entity) <= radiusXZ * radiusXZ) {
                            greatsword.freeze(entity, (int) Math.min(60, 12 * event.getDistance()), 0.4F * event.getDistance());
                            froze = true;
                        }
                    }

                    if (froze && !nearbyEntities.isEmpty()) {
                        var world = player.getWorld();

                        for (double i = 0; i <= 2 * radiusXZ; i += radiusXZ / 48D) {
                            var x = radiusXZ - i;
                            var z = Math.sqrt((radiusXZ * radiusXZ - x * x));
                            var particles = 1;

                            world.spawnParticles(
                                ParticleTypes.ITEM_SNOWBALL,
                                player.getX() + x,
                                player.getEyeY(),
                                player.getZ() + z,
                                particles, 0, 0, 0, 0D
                            );

                            world.spawnParticles(
                                ParticleTypes.ITEM_SNOWBALL,
                                player.getX() + x,
                                player.getEyeY(),
                                player.getZ() - z,
                                particles, 0, 0, 0, 0D
                            );
                        }
                    }
                }
            }
        }
    }

    /**
     Tick components.
     */
    @SubscribeEvent
    public static void livingTick(LivingEvent.LivingUpdateEvent event) {
        var entityData = Components.entityData.of(event.getEntity());

        if (entityData.isFrozen() /*&& !event.getEntity().world.isClient*/) {
            event.setCanceled(true);
        }

        entityData.tick();

        if (event.getEntity() instanceof PlayerEntity player) {
            Components.soulbound(player).forEach(SoulboundComponent::tick);
        }
    }

    /**
     Prevent entities afflicted with {@link Skills#endermanacle} from teleporting.
     */
    @SubscribeEvent
    public static void teleport(EntityTeleportEvent event) {
        if (Components.entityData.of(event.getEntity()).cannotTeleport()) {
            event.setCanceled(true);
        }
    }

    /**
     Register the command and enable command errors debug logging in development environments.
     */
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        SoulboundArmoryCommand.register(event);

        if (ManagementFactory.getRuntimeMXBean().getInputArguments().stream().anyMatch(argument -> argument.startsWith("-agentlib:jdwp"))) {
            Accessor.<Logger>getReference(CommandManager.class, "LOGGER").setLevel(Level.DEBUG);
        }
    }
}
