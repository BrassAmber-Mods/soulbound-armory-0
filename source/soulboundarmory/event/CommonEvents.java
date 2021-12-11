package soulboundarmory.event;

import java.lang.management.ManagementFactory;
import net.auoeke.reflect.Accessor;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.command.SoulboundArmoryCommand;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.item.weapon.WeaponComponent;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.config.Configuration;
import soulboundarmory.entity.SoulboundDaggerEntity;
import soulboundarmory.entity.SoulboundFireballEntity;
import soulboundarmory.entity.SoulboundLightningEntity;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.registry.Skills;
import soulboundarmory.registry.SoulboundItems;
import soulboundarmory.util.EntityUtil;
import soulboundarmory.util.ItemUtil;

@EventBusSubscriber(modid = SoulboundArmory.ID)
public final class CommonEvents {
    @SubscribeEvent
    public static void onPlayerDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof PlayerEntity player && !player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            Components.soulbound(player)
                .map(SoulboundComponent::item)
                .filter(component -> component != null && component.intValue(StatisticType.level) >= Configuration.instance().preservationLevel)
                .forEach(component -> event.getDrops().removeIf(drop -> component.accepts(drop.getStack()) && player.giveItemStack(drop.getStack())));
        }
    }

    /*
    @SubscribeEvent
    public static void onUseBlock(UseBlockEvent event) {
        PlayerEntity player = event.getPlayer();
        ItemStack mainStack = player.getMainHandStack();

        if (mainStack.getItem() instanceof SoulboundWeaponItem && mainStack != event.getItemStack()) {
            event.setFail();
        }
    }
    */

    @SubscribeEvent
    public static void onBlockBreakSpeed(PlayerEvent.BreakSpeed event) {
        var player = event.getPlayer();
        ItemComponent.get(player, player.getMainHandStack()).ifPresent(component -> {
            var efficiency = component.statistic(StatisticType.efficiency);

            if (efficiency != null) {
                event.setNewSpeed(event.getNewSpeed() * efficiency.floatValue());
            }
        });
    }

    /**
     Cancel damage to leaping players, determine whether player hits are critical and apply {@link Skills#nourishment}.
     */
    @SubscribeEvent
    public static void damage(LivingDamageEvent event) {
        var damage = event.getSource();
        var target = event.getEntityLiving();

        if (!target.world.isClient) {
            if (target instanceof PlayerEntity && ItemComponentType.greatsword.get(target).leapForce() > 0 && !damage.isExplosive() && !damage.isProjectile()) {
                event.setCanceled(true);
            }

            if (damage.getAttacker() instanceof PlayerEntity player) {
                var source = damage.getSource();
                var instance = Components.weapon.of(player);
                ItemComponent<?> storage;

                if (source instanceof SoulboundDaggerEntity) {
                    storage = instance.item(ItemComponentType.dagger);
                } else if (source instanceof PlayerEntity) {
                    storage = instance.item();
                } else if (source instanceof SoulboundLightningEntity) {
                    storage = instance.item(ItemComponentType.sword);
                } else if (source instanceof SoulboundFireballEntity) {
                    storage = instance.item(ItemComponentType.staff);
                } else {
                    return;
                }

                if (storage != null) {
                    var amount = event.getAmount();

                    if (((WeaponComponent<?>) storage).hit()) {
                        event.setAmount(amount *= 2);
                        Packets.clientCriticalHitParticles.sendNearby(target, new ExtendedPacketBuffer().writeEntity(target));
                    }

                    if (storage.hasSkill(Skills.nourishment)) {
                        var value = (1 + storage.skill(Skills.nourishment).level()) * amount / 20F;
                        player.getHungerManager().add((int) (Math.ceil(value + 1) / 2), 1.5F * value);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void death(LivingDeathEvent event) {
        var entity = event.getEntityLiving();
        var attacker = event.getSource().getAttacker();

        if (attacker == null) {
            attacker = entity.getDamageTracker().getBiggestAttacker();
        }

        if (attacker instanceof PlayerEntity player && !player.world.isClient) {
            var damageAttribute = entity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            var damage = damageAttribute == null ? 0 : damageAttribute.getValue();
            var immediateSource = event.getSource().getSource();
            var component = Components.weapon.of(player);
            ItemComponent<?> storage;
            Text displayName;

            if (immediateSource instanceof SoulboundDaggerEntity dagger) {
                storage = component.item(ItemComponentType.dagger);
                displayName = dagger.itemStack.getName();
            } else if (immediateSource instanceof SoulboundLightningEntity) {
                storage = component.item(ItemComponentType.sword);
                displayName = ItemUtil.equippedStack(player, SoulboundItems.sword).getName();
            } else {
                storage = component.item();
                displayName = player.getMainHandStack().getName();
            }

            if (storage != null) {
                var configuration = Configuration.instance();

                var xp = entity.getMaxHealth()
                    * player.world.getDifficulty().getId() * configuration.difficultyMultiplier
                    * (1 + entity.getAttributeValue(EntityAttributes.GENERIC_ARMOR) * configuration.armorMultiplier);

                xp *= damage <= 0 ? configuration.passiveMultiplier : 1 + damage * configuration.attackDamageMultiplier;

                if (EntityUtil.isBoss(entity)) {
                    xp *= configuration.bossMultiplier;
                }

                if (player.world.getServer().isHardcore()) {
                    xp *= configuration.hardcoreMultiplier;
                }

                if (damage > 0 && entity.isBaby()) {
                    xp *= configuration.babyMultiplier;
                }

                if (storage.incrementStatistic(StatisticType.experience, Math.round(xp)) && Components.config.of(player).levelupNotifications) {
                    player.sendMessage(Translations.levelupMessage.format(displayName, storage.intValue(StatisticType.level)), true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void fall(LivingFallEvent event) {
        var fallen = event.getEntity();

        if (!fallen.world.isClient && fallen instanceof PlayerEntity player) {
            var component = Components.weapon.of(player);
            var storage = component.item(ItemComponentType.greatsword);
            var leapForce = storage.leapForce();

            if (leapForce > 0) {
                if (storage.hasSkill(Skills.freezing)) {
                    var radiusXZ = Math.min(4, event.getDistance());
                    var radiusY = Math.min(2, 0.5F * event.getDistance());
                    var nearbyEntities = player.world.getOtherEntities(player, new Box(
                        player.getX() - radiusXZ, player.getY() - radiusY, player.getZ() - radiusXZ,
                        player.getX() + radiusXZ, player.getY() + radiusY, player.getZ() + radiusXZ
                    ));

                    var froze = false;

                    for (var entity : nearbyEntities) {
                        if (entity.squaredDistanceTo(entity) <= radiusXZ * radiusXZ) {
                            storage.freeze(entity, (int) Math.min(60, 12 * event.getDistance()), 0.4F * event.getDistance());
                            froze = true;
                        }
                    }

                    if (froze && !nearbyEntities.isEmpty()) {
                        var world = (ServerWorld) player.world;

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

                if (event.getDistance() <= 16 * leapForce) {
                    event.setCanceled(true);
                } else {
                    event.setDamageMultiplier(event.getDistance() / (float) (Math.max(1, Math.log(4 * leapForce) / Math.log(2))));
                }

                storage.leapDuration(4);
            }
        }
    }

    @SubscribeEvent
    public static void tick(LivingEvent.LivingUpdateEvent event) {
        var entityData = Components.entityData.of(event.getEntity());
        entityData.tick();

        if (entityData.isFrozen()) {
            event.setCanceled(true);
        }

        if (event.getEntity() instanceof PlayerEntity player) {
            Components.soulbound(player).forEach(SoulboundComponent::tick);
        }
    }

    @SubscribeEvent
    public static void teleport(EntityTeleportEvent event) {
        if (Components.entityData.of(event.getEntity()).cannotTeleport()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        SoulboundArmoryCommand.register(event);

        if (ManagementFactory.getRuntimeMXBean().getInputArguments().stream().anyMatch(argument -> argument.startsWith("-agentlib:jdwp"))) {
            Accessor.<Logger>getObject(CommandManager.class, "LOGGER").setLevel(Level.DEBUG);
        }
    }
}
