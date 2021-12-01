package soulboundarmory.event;

import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
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
import soulboundarmory.registry.Packets;
import soulboundarmory.registry.Skills;
import soulboundarmory.registry.SoulboundItems;
import soulboundarmory.util.ItemUtil;
import soulboundarmory.util.Util;

@EventBusSubscriber(modid = SoulboundArmory.ID)
public class EntityEvents {
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
    public static void spawn(PlayerEvent.PlayerLoggedInEvent event) {
        var player = event.getPlayer();

        if (!player.level.isClientSide) {
            Packets.serverConfig.send(player, new ExtendedPacketBuffer().writeBoolean(Components.config.of(player).levelupNotifications));
        }
    }

    @SubscribeEvent
    public static void onPlayerDrops(LivingDropsEvent event) {
        var entity = event.getEntity();

        if (entity instanceof PlayerEntity) {
            var player = (PlayerEntity) event.getEntity();

            if (!player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
                Components.soulbound(player)
                    .map(SoulboundComponent::storage)
                    .filter(storage -> storage != null && storage.datum(StatisticType.level) >= Configuration.instance().preservationLevel)
                    .forEach(storage -> {
                        var drops = event.getDrops();

                        for (var item : drops) {
                            var itemStack = item.getItem();

                            if (storage.itemClass().isInstance(itemStack.getItem()) && player.addItem(itemStack)) {
                                drops.remove(item);
                            }
                        }
                    });
            }
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

            if (!player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
                var storage = currentComponent.storage();

                if (storage != null && storage.datum(StatisticType.level) >= Configuration.instance().preservationLevel) {
                    player.addItem(storage.stack());
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
        var entity = event.getPlayer();
        var tool = entity.getMainHandItem();

        if (tool.getItem() == SoulboundItems.pick && Components.tool.of(entity).storage().hasSkill(Skills.enderPull)) {
            event.setCanceled(true);
            event.getWorld().removeBlock(event.getPos(), false);

            Block.dropResources(event.getState(), event.getWorld(), entity.blockPosition(), null);
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

        if (entity instanceof PlayerEntity && !entity.level.isClientSide && Components.weapon.of(entity).storage(StorageType.greatsword).leapForce() > 0) {
            var damageSource = event.getSource();

            if (!damageSource.isExplosion() && !damageSource.isProjectile()) {
                event.setCanceled(true);
            }
        }

        if (entity instanceof PlayerEntity && !entity.level.isClientSide) {
            var source = event.getSource().getDirectEntity();
            var instance = Components.weapon.of(entity);
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
                storage = null;
            }

            if (storage != null) {
                var random = entity.level.random;
                var attackDamage = storage.attribute(StatisticType.criticalStrikeRate) > random.nextDouble() ? 2 * event.getAmount() : event.getAmount();

                if (storage.hasSkill(Skills.nourishment)) {
                    var leeching = storage.skill(Skills.nourishment);
                    var saturation = (1 + leeching.level()) * attackDamage / 20F;
                    var food = random.nextInt((int) Math.ceil(saturation) + 1);

                    ((PlayerEntity) entity).getFoodData().eat(food, 2 * saturation);
                }

                event.setAmount(attackDamage);
            }
        }
    }

    @SubscribeEvent
    public static void death(LivingDeathEvent event) {
        var entity = event.getEntityLiving();
        var attacker = event.getSource().getEntity();

        if (attacker == null) {
            attacker = entity.getCombatTracker().getKiller();
        } else if (attacker != entity.getCombatTracker().getKiller()) {
            throw new RuntimeException();
        }

        if ((attacker instanceof PlayerEntity player) && !attacker.level.isClientSide) {
            var damageAttribute = entity.getAttribute(Attributes.ATTACK_DAMAGE);
            var damage = damageAttribute == null ? 0 : damageAttribute.getValue();
            var armor = entity.getAttributeValue(Attributes.ARMOR);
            var immediateSource = event.getSource().getDirectEntity();
            var capability = Components.weapon.of(player);
            ItemStorage<?> storage;
            ITextComponent displayName;

            if (immediateSource instanceof SoulboundDaggerEntity) {
                storage = capability.storage(StorageType.dagger);
                displayName = ((SoulboundDaggerEntity) immediateSource).itemStack.getDisplayName();
            } else if (immediateSource instanceof SoulboundLightningEntity) {
                storage = capability.storage(StorageType.sword);
                displayName = ItemUtil.equippedStack(player, SoulboundItems.sword).getDisplayName();
            } else {
                storage = capability.storage();
                displayName = player.getMainHandItem().getDisplayName();
            }

            if (storage != null) {
                var configuration = Configuration.instance();

                var xp = entity.getMaxHealth()
                    * attacker.level.getDifficulty().getId() * configuration.difficultyMultiplier
                    * (1 + armor * configuration.armorMultiplier);

                xp *= damage <= 0 ? configuration.passiveMultiplier : 1 + damage * configuration.attackDamageMultiplier;

                if (((EntityAccess) entity).soulboundarmory$isBoss()) {
                    xp *= configuration.bossMultiplier;
                }

                if (Util.server().isHardcore()) {
                    xp *= configuration.hardcoreMultiplier;
                }

                if (damage > 0 && entity.isBaby()) {
                    xp *= configuration.babyMultiplier;
                }

                var configCapability = Components.config.of(player);

                if (storage.incrementStatistic(StatisticType.experience, (int) Math.round(xp)) && configCapability.levelupNotifications) {
                    ((PlayerEntity) attacker).displayClientMessage(Translations.messageLevelUp.format(displayName, storage.datum(StatisticType.level)), true);
                }

                storage.sync();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void fall(LivingFallEvent event) {
        var fallen = event.getEntity();

        if (!fallen.level.isClientSide && fallen instanceof PlayerEntity player) {
            var component = Components.weapon.of(player);
            var storage = component.storage(StorageType.greatsword);
            var leapForce = storage.leapForce();

            if (leapForce > 0) {
                if (storage.hasSkill(Skills.freezing)) {
                    var radiusXZ = Math.min(4, event.getDistance());
                    var radiusY = Math.min(2, 0.5F * event.getDistance());
                    var nearbyEntities = player.level.getEntities(player, new AxisAlignedBB(
                        player.getX() - radiusXZ, player.getY() - radiusY, player.getZ() - radiusXZ,
                        player.getX() + radiusXZ, player.getY() + radiusY, player.getZ() + radiusXZ
                    ));

                    var froze = false;

                    for (var entity : nearbyEntities) {
                        if (entity.distanceToSqr(entity) <= radiusXZ * radiusXZ) {
                            storage.freeze(entity, (int) Math.min(60, 12 * event.getDistance()), 0.4F * event.getDistance());

                            froze = true;
                        }
                    }

                    if (froze) {
                        if (!nearbyEntities.isEmpty()) {
                            var world = (ServerWorld) player.level;

                            for (double i = 0; i <= 2 * radiusXZ; i += radiusXZ / 48D) {
                                var x = radiusXZ - i;
                                var z = Math.sqrt((radiusXZ * radiusXZ - x * x));
                                var particles = 1;

                                world.sendParticles(ParticleTypes.ITEM_SNOWBALL,
                                    player.getX() + x,
                                    player.getEyeY(),
                                    player.getZ() + z,
                                    particles, 0, 0, 0, 0D
                                );
                                world.sendParticles(ParticleTypes.ITEM_SNOWBALL,
                                    player.getX() + x,
                                    player.getEyeY(),
                                    player.getZ() - z,
                                    particles, 0, 0, 0, 0D
                                );
                            }
                        }
                    }
                }

                if (event.getDistance() <= 16 * leapForce) {
                    event.setCanceled(true);
                } else {
                    var multiplier = event.getDistance() / (float) (Math.max(1, Math.log(4 * leapForce) / Math.log(2)));

                    event.setDamageMultiplier(multiplier);
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
}
