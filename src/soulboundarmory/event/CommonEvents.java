package soulboundarmory.event;

import java.util.Objects;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.command.SoulboundArmoryCommand;
import soulboundarmory.component.Component;
import soulboundarmory.component.ComponentRegistry;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.soulbound.item.StorageType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.config.Configuration;
import soulboundarmory.entity.SAAttributes;
import soulboundarmory.entity.SoulboundDaggerEntity;
import soulboundarmory.entity.SoulboundFireballEntity;
import soulboundarmory.entity.SoulboundLightningEntity;
import soulboundarmory.mixin.access.entity.EntityAccess;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.registry.Skills;
import soulboundarmory.registry.SoulboundItems;
import soulboundarmory.util.ItemUtil;

@EventBusSubscriber(modid = SoulboundArmory.ID)
public class CommonEvents {
    @SubscribeEvent
    public static void construct(EntityEvent.EntityConstructing event) {
        var entity = event.getEntity();

        ComponentRegistry.registry.forEach(key -> {
            if (key.type.isInstance(entity)) {
                ((EntityAccess) entity).soulboundarmory$components().put(key, ((Function<Entity, Component>) key.instantiate).apply(entity));
            }
        });
    }

    @SubscribeEvent
    public static void login(ClientPlayerNetworkEvent.LoggedInEvent event) {
        Packets.serverConfig.send(new ExtendedPacketBuffer().writeBoolean(Components.config.of(event.getPlayer()).levelupNotifications));
    }

    @SubscribeEvent
    public static void login(PlayerEvent.PlayerLoggedInEvent event) {
        var player = event.getPlayer();

        if (!player.world.isClient) {
            Components.soulbound(player).map(SoulboundComponent::storage).filter(Objects::nonNull).forEach(ItemStorage::sync);
        }
    }

    @SubscribeEvent
    public static void onPlayerDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof PlayerEntity player && !player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            Components.soulbound(player)
                .map(SoulboundComponent::storage)
                .filter(storage -> storage != null && storage.datum(StatisticType.level) >= Configuration.instance().preservationLevel)
                .forEach(storage -> {
                    var drops = event.getDrops();

                    for (var item : drops) {
                        var itemStack = item.getStack();

                        if (storage.itemClass().isInstance(itemStack.getItem()) && player.giveItemStack(itemStack)) {
                            drops.remove(item);
                        }
                    }
                });
        }
    }

    @SubscribeEvent
    public static void onPlayerCopy(PlayerEvent.Clone event) {
        var original = event.getOriginal();
        var player = event.getPlayer();

        for (var type : Components.soulboundComponents) {
            var originalComponent = type.of(original);
            var currentComponent = type.of(player);

            currentComponent.deserialize(originalComponent.serialize());

            if (!player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
                var storage = currentComponent.storage();

                if (storage != null && storage.datum(StatisticType.level) >= Configuration.instance().preservationLevel) {
                    player.giveItemStack(storage.stack());
                }
            }
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

        if (player != null) {
            event.setNewSpeed(event.getNewSpeed() * (float) player.getAttributeValue(SAAttributes.efficiency));
        }
    }

    @SubscribeEvent
    public static void onBlockDrop(BlockEvent.BreakEvent event) {
        var player = event.getPlayer();
        var tool = player.getMainHandStack();

        if (tool.getItem() == SoulboundItems.pick && Components.tool.of(player).storage().hasSkill(Skills.enderPull)) {
            event.setCanceled(true);
            event.getWorld().removeBlock(event.getPos(), false);

            Block.dropStacks(event.getState(), event.getWorld(), player.getBlockPos(), null);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onEntityItemPickup(PlayerEvent.ItemPickupEvent event) {
/*
         ItemEntity entity = event.getItemEntity();
         ItemStack stack = entity.getStack();
         PlayerEntity player = event.getPlayer();

        player.sendPickup(entity, stack.getCount());
        SoulboundItemUtil.addItemStack(stack, player);

        event.setFail();
*/
    }

    @SubscribeEvent
    public static void damage(LivingDamageEvent event) {
        var entity = event.getEntity();

        if (entity instanceof PlayerEntity && !entity.world.isClient && Components.weapon.of(entity).storage(StorageType.greatsword).leapForce() > 0) {
            var damageSource = event.getSource();

            if (!damageSource.isExplosive() && !damageSource.isProjectile()) {
                event.setCanceled(true);
            }
        }

        if (entity instanceof PlayerEntity player && !player.world.isClient) {
            var source = event.getSource().getSource();
            var instance = Components.weapon.of(player);
            ItemStorage<?> storage;

            if (source instanceof SoulboundDaggerEntity) {
                storage = instance.storage(StorageType.dagger);
            } else if (source instanceof PlayerEntity) {
                storage = instance.storage();
            } else if (source instanceof SoulboundLightningEntity) {
                storage = instance.storage(StorageType.sword);
            } else if (source instanceof SoulboundFireballEntity) {
                storage = instance.storage(StorageType.staff);
            } else {
                return;
            }

            if (storage != null) {
                var random = player.world.random;
                var attackDamage = storage.attribute(StatisticType.criticalStrikeRate) > random.nextDouble() ? 2 * event.getAmount() : event.getAmount();

                if (storage.hasSkill(Skills.nourishment)) {
                    var leeching = storage.skill(Skills.nourishment);
                    var saturation = (1 + leeching.level()) * attackDamage / 20F;
                    var food = random.nextInt((int) Math.ceil(saturation) + 1);

                    player.getHungerManager().add(food, 2 * saturation);
                }

                event.setAmount(attackDamage);
            }
        }
    }

    @SubscribeEvent
    public static void death(LivingDeathEvent event) {
        var entity = event.getEntityLiving();
        var attacker = event.getSource().getAttacker();

        if (attacker == null) {
            attacker = entity.getDamageTracker().getBiggestAttacker();
        } else if (attacker != entity.getDamageTracker().getBiggestAttacker()) {
            throw new RuntimeException();
        }

        if (attacker instanceof PlayerEntity player && !player.world.isClient) {
            var damageAttribute = entity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            var damage = damageAttribute == null ? 0 : damageAttribute.getValue();
            var immediateSource = event.getSource().getSource();
            var component = Components.weapon.of(player);
            ItemStorage<?> storage;
            Text displayName;

            if (immediateSource instanceof SoulboundDaggerEntity dagger) {
                storage = component.storage(StorageType.dagger);
                displayName = dagger.itemStack.getName();
            } else if (immediateSource instanceof SoulboundLightningEntity) {
                storage = component.storage(StorageType.sword);
                displayName = ItemUtil.equippedStack(player, SoulboundItems.sword).getName();
            } else {
                storage = component.storage();
                displayName = player.getMainHandStack().getName();
            }

            if (storage != null) {
                var configuration = Configuration.instance();

                var xp = entity.getMaxHealth()
                    * player.world.getDifficulty().getId() * configuration.difficultyMultiplier
                    * (1 + entity.getAttributeValue(EntityAttributes.GENERIC_ARMOR) * configuration.armorMultiplier);

                xp *= damage <= 0 ? configuration.passiveMultiplier : 1 + damage * configuration.attackDamageMultiplier;

                if (((EntityAccess) entity).soulboundarmory$isBoss()) {
                    xp *= configuration.bossMultiplier;
                }

                if (player.world.getServer().isHardcore()) {
                    xp *= configuration.hardcoreMultiplier;
                }

                if (damage > 0 && entity.isBaby()) {
                    xp *= configuration.babyMultiplier;
                }

                if (storage.incrementStatistic(StatisticType.experience, Math.round(xp)) && Components.config.of(player).levelupNotifications) {
                    player.sendMessage(Translations.messageLevelUp.format(displayName, storage.datum(StatisticType.level)), true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void fall(LivingFallEvent event) {
        var fallen = event.getEntity();

        if (!fallen.world.isClient && fallen instanceof PlayerEntity player) {
            var component = Components.weapon.of(player);
            var storage = component.storage(StorageType.greatsword);
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

                            world.spawnParticles(ParticleTypes.ITEM_SNOWBALL,
                                player.getX() + x,
                                player.getEyeY(),
                                player.getZ() + z,
                                particles, 0, 0, 0, 0D
                            );
                            world.spawnParticles(ParticleTypes.ITEM_SNOWBALL,
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
            Components.tool.of(player).tick();
            Components.weapon.of(player).tick();
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
    }
}
