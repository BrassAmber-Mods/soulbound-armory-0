package user11681.soulboundarmory.event;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
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
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.asm.access.entity.BossEntityAccess;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.capability.CapabilityContainer;
import user11681.soulboundarmory.capability.config.ConfigCapability;
import user11681.soulboundarmory.capability.entity.EntityData;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.capability.soulbound.item.weapon.GreatswordStorage;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.entity.SAAttributes;
import user11681.soulboundarmory.entity.SoulboundDaggerEntity;
import user11681.soulboundarmory.entity.SoulboundFireballEntity;
import user11681.soulboundarmory.entity.SoulboundLightningEntity;
import user11681.soulboundarmory.registry.Skills;
import user11681.soulboundarmory.registry.SoulboundItems;
import user11681.soulboundarmory.skill.SkillContainer;
import user11681.soulboundarmory.util.ItemUtil;
import user11681.soulboundarmory.util.Util;

import static user11681.soulboundarmory.capability.statistics.StatisticType.criticalStrikeProbability;
import static user11681.soulboundarmory.capability.statistics.StatisticType.experience;
import static user11681.soulboundarmory.capability.statistics.StatisticType.level;
import static user11681.soulboundarmory.registry.Skills.enderPull;

@EventBusSubscriber(modid = SoulboundArmory.ID)
public class EntityEvents {
    @SubscribeEvent
    public static void onPlayerDrops(LivingDropsEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();

            if (!player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
                for (CapabilityContainer<? extends SoulboundCapability> type : Capabilities.soulboundCapabilities) {
                    ItemStorage<?> storage = type.get(player).storage();

                    if (storage != null && storage.datum(level) >= Configuration.instance().preservationLevel) {
                        Collection<ItemEntity> drops = event.getDrops();

                        for (ItemEntity item : drops) {
                            ItemStack itemStack = item.getItem();

                            if (storage.getBaseItemClass().isInstance(itemStack.getItem()) && player.addItem(itemStack)) {
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
        PlayerEntity original = event.getOriginal();
        PlayerEntity player = event.getPlayer();

        for (CapabilityContainer<? extends SoulboundCapability> type : Capabilities.soulboundCapabilities) {
            SoulboundCapability originalComponent = type.get(original);
            SoulboundCapability currentComponent = type.get(player);

            currentComponent.deserializeNBT(originalComponent.serializeNBT());

            if (!player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
                ItemStorage<?> storage = currentComponent.storage();

                if (storage != null && storage.datum(level) >= Configuration.instance().preservationLevel) {
                    player.addItem(storage.getItemStack());
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
        PlayerEntity player = event.getPlayer();

        if (player != null) {
            event.setNewSpeed(event.getNewSpeed() * (float) player.getAttributeValue(SAAttributes.efficiency));
        }
    }

    @SubscribeEvent
    public static void onBlockDrop(BlockEvent.BreakEvent event) {
        PlayerEntity entity = event.getPlayer();
        ItemStack tool = entity.getMainHandItem();

        if (tool.getItem() == SoulboundItems.pick && Capabilities.tool.get(entity).storage().hasSkill(enderPull)) {
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
        Entity entity = event.getEntity();

        if (entity instanceof PlayerEntity && !entity.level.isClientSide && Capabilities.weapon.get(entity).storage(StorageType.greatsword).leapForce() > 0) {
            DamageSource damageSource = event.getSource();

            if (!damageSource.isExplosion() && !damageSource.isProjectile()) {
                event.setCanceled(true);
            }
        }

        if (entity instanceof PlayerEntity && !entity.level.isClientSide) {
            Entity source = event.getSource().getEntity();
            SoulboundCapability instance = Capabilities.weapon.get(entity);
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
                Random random = entity.level.random;
                float attackDamage = storage.attribute(criticalStrikeProbability) > random.nextDouble() ? 2 * event.getAmount() : event.getAmount();

                if (storage.hasSkill(Skills.nourishment)) {
                    SkillContainer leeching = storage.skill(Skills.nourishment);
                    float saturation = (1 + leeching.level()) * attackDamage / 20F;
                    int food = random.nextInt((int) Math.ceil(saturation) + 1);

                    ((PlayerEntity) entity).getFoodData().eat(food, 2 * saturation);
                }

                event.setAmount(attackDamage);
            }
        }
    }

    @SubscribeEvent
    public static void death(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        Entity attacker = event.getSource().getDirectEntity();

        if (attacker == null) {
            attacker = entity.getCombatTracker().getKiller();
        }

        if ((attacker instanceof PlayerEntity) && !attacker.level.isClientSide) {
            PlayerEntity player = (PlayerEntity) attacker;
            ModifiableAttributeInstance attackDamageAttribute = entity.getAttribute(Attributes.ATTACK_DAMAGE);
            double attackDamage = attackDamageAttribute == null ? 0 : attackDamageAttribute.getValue();
            double armor = entity.getAttributeValue(Attributes.ARMOR);
            Entity immediateSource = event.getSource().getEntity();
            SoulboundCapability component = Capabilities.weapon.get(attacker);
            ItemStorage<?> storage;
            ITextComponent displayName;

            if (immediateSource instanceof SoulboundDaggerEntity) {
                storage = component.storage(StorageType.dagger);
                displayName = ((SoulboundDaggerEntity) immediateSource).itemStack.getHoverName();
            } else if (immediateSource instanceof SoulboundLightningEntity) {
                storage = component.storage(StorageType.sword);
                displayName = ItemUtil.equippedStack(player, SoulboundItems.sword).getHoverName();
            } else {
                storage = component.storage();
                displayName = player.getMainHandItem().getHoverName();
            }

            if (storage != null) {
                Configuration configuration = Configuration.instance();

                double xp = entity.getMaxHealth()
                    * attacker.level.getDifficulty().getId() * configuration.difficultyMultiplier
                    * (1 + armor * configuration.armorMultiplier);

                xp *= attackDamage <= 0 ? configuration.passiveMultiplier : 1 + attackDamage * configuration.attackDamageMultiplier;

                if (((BossEntityAccess) entity).isBoss()) {
                    xp *= configuration.bossMultiplier;
                }

                if (Util.server().isHardcore()) {
                    xp *= configuration.hardcoreMultiplier;
                }

                if (attackDamage > 0 && entity.isBaby()) {
                    xp *= configuration.babyMultiplier;
                }

                ConfigCapability configCapability = Capabilities.config.get(player);

                if (storage.incrementStatistic(experience, (int) Math.round(xp)) && configCapability.levelupNotifications) {
                    ((PlayerEntity) attacker).displayClientMessage(Translations.messageLevelUp.format(displayName, storage.datum(level)), true);
                }

                // capability.sync();
                storage.sync();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void fall(LivingFallEvent event) {
        Entity fallen = event.getEntity();

        if (!fallen.level.isClientSide && fallen instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) fallen;
            SoulboundCapability component = Capabilities.weapon.get(player);
            GreatswordStorage storage = component.storage(StorageType.greatsword);
            double leapForce = storage.leapForce();

            if (leapForce > 0) {
                if (storage.hasSkill(Skills.freezing)) {
                    double radiusXZ = Math.min(4, event.getDistance());
                    double radiusY = Math.min(2, 0.5F * event.getDistance());
                    List<Entity> nearbyEntities = player.level.getEntities(player, new AxisAlignedBB(
                        player.getX() - radiusXZ, player.getY() - radiusY, player.getZ() - radiusXZ,
                        player.getX() + radiusXZ, player.getY() + radiusY, player.getZ() + radiusXZ
                    ));

                    boolean froze = false;

                    for (Entity entity : nearbyEntities) {
                        if (entity.distanceToSqr(entity) <= radiusXZ * radiusXZ) {
                            storage.freeze(entity, (int) Math.min(60, 12 * event.getDistance()), 0.4F * event.getDistance());

                            froze = true;
                        }
                    }

                    if (froze) {
                        if (!nearbyEntities.isEmpty()) {
                            ServerWorld world = (ServerWorld) player.level;

                            for (double i = 0; i <= 2 * radiusXZ; i += radiusXZ / 48D) {
                                double x = radiusXZ - i;
                                double z = Math.sqrt((radiusXZ * radiusXZ - x * x));
                                int particles = 1;

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
                    float multiplier = event.getDistance() / (float) (Math.max(1, Math.log(4 * leapForce) / Math.log(2)));

                    event.setDamageMultiplier(multiplier);
                }

                storage.leapDuration(4);
            }
        }
    }

    @SubscribeEvent
    public static void livingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityData entityData = Capabilities.entityData.get(event.getEntity());
        entityData.tick();

        if (entityData.isFrozen()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void teleport(EntityTeleportEvent event) {
        EntityData component = Capabilities.entityData.get(event.getEntity());

        if (component.cannotTeleport()) {
            event.setCanceled(true);
        }
    }
}
