package net.auoeke.soulboundarmory.event;

import net.auoeke.soulboundarmory.SoulboundArmory;
import net.auoeke.soulboundarmory.mixin.access.entity.BossEntityAccess;
import net.auoeke.soulboundarmory.capability.Capabilities;
import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.auoeke.soulboundarmory.capability.soulbound.item.StorageType;
import net.auoeke.soulboundarmory.capability.statistics.StatisticType;
import net.auoeke.soulboundarmory.client.i18n.Translations;
import net.auoeke.soulboundarmory.config.Configuration;
import net.auoeke.soulboundarmory.entity.SAAttributes;
import net.auoeke.soulboundarmory.entity.SoulboundDaggerEntity;
import net.auoeke.soulboundarmory.entity.SoulboundFireballEntity;
import net.auoeke.soulboundarmory.entity.SoulboundLightningEntity;
import net.auoeke.soulboundarmory.registry.Skills;
import net.auoeke.soulboundarmory.registry.SoulboundItems;
import net.auoeke.soulboundarmory.util.ItemUtil;
import net.auoeke.soulboundarmory.util.Util;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
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

@EventBusSubscriber(modid = SoulboundArmory.ID)
public class EntityEvents {
    @SubscribeEvent
    public static void onPlayerDrops(LivingDropsEvent event) {
        var entity = event.getEntity();

        if (entity instanceof PlayerEntity) {
            var player = (PlayerEntity) event.getEntity();

            if (!player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
                for (var type : Capabilities.soulboundCapabilities) {
                    var storage = type.get(player).get().storage();

                    if (storage != null && storage.datum(StatisticType.level) >= Configuration.instance().preservationLevel) {
                        var drops = event.getDrops();

                        for (var item : drops) {
                            var itemStack = item.getItem();

                            if (storage.itemClass().isInstance(itemStack.getItem()) && player.addItem(itemStack)) {
                                drops.remove(item);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCopy(PlayerEvent.Clone event) {
        var original = event.getOriginal();
        var player = event.getPlayer();

        for (var type : Capabilities.soulboundCapabilities) {
            var originalComponent = type.get(original).get();
            var currentComponent = type.get(player).get();

            currentComponent.deserializeNBT(originalComponent.serializeNBT());

            if (!player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
                var storage = currentComponent.storage();

                if (storage != null && storage.datum(StatisticType.level) >= Configuration.instance().preservationLevel) {
                    player.addItem(storage.itemStack());
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

        if (tool.getItem() == SoulboundItems.pick && Capabilities.tool.get(entity).get().storage().hasSkill(Skills.enderPull)) {
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

        if (entity instanceof PlayerEntity && !entity.level.isClientSide && Capabilities.weapon.get(entity).get().storage(StorageType.greatsword).leapForce() > 0) {
            var damageSource = event.getSource();

            if (!damageSource.isExplosion() && !damageSource.isProjectile()) {
                event.setCanceled(true);
            }
        }

        if (entity instanceof PlayerEntity && !entity.level.isClientSide) {
            var source = event.getSource().getDirectEntity();
            var instance = Capabilities.weapon.get(entity).get();
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
            var capability = Capabilities.weapon.get(player).get();
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

                if (((BossEntityAccess) entity).isBoss()) {
                    xp *= configuration.bossMultiplier;
                }

                if (Util.server().isHardcore()) {
                    xp *= configuration.hardcoreMultiplier;
                }

                if (damage > 0 && entity.isBaby()) {
                    xp *= configuration.babyMultiplier;
                }

                var configCapability = Capabilities.config.get(player).get();

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
            var component = Capabilities.weapon.get(player).get();
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
        var entityData = Capabilities.entityData.get(event.getEntity()).get();
        entityData.tick();

        if (entityData.isFrozen()) {
            event.setCanceled(true);
        }

        if (event.getEntity() instanceof PlayerEntity player) {
            Capabilities.tool.get(player).get().tick();
            Capabilities.weapon.get(player).get().tick();
        }
    }

    @SubscribeEvent
    public static void teleport(EntityTeleportEvent event) {
        if (Capabilities.entityData.get(event.getEntity()).get().cannotTeleport()) {
            event.setCanceled(true);
        }
    }
}
