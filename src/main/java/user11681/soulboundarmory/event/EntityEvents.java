package user11681.soulboundarmory.event;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;
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

import static user11681.soulboundarmory.capability.statistics.StatisticType.criticalStrikeRate;
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

            if (!player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
                for (CapabilityContainer<? extends SoulboundCapability> type : Capabilities.soulboundCapabilities) {
                    ItemStorage<?> storage = type.get(player).storage();

                    if (storage != null && storage.datum(level) >= Configuration.instance().preservationLevel) {
                        Collection<ItemEntity> drops = event.getDrops();

                        for (ItemEntity item : drops) {
                            ItemStack itemStack = item.getStack();

                            if (storage.itemClass().isInstance(itemStack.getItem()) && player.giveItemStack(itemStack)) {
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

            if (!player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
                ItemStorage<?> storage = currentComponent.storage();

                if (storage != null && storage.datum(level) >= Configuration.instance().preservationLevel) {
                    player.giveItemStack(storage.itemStack());
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
        ItemStack tool = entity.getMainHandStack();

        if (tool.getItem() == SoulboundItems.pick && Capabilities.tool.get(entity).storage().hasSkill(enderPull)) {
            event.setCanceled(true);
            event.getWorld().removeBlock(event.getPos(), false);

            Block.dropStacks(event.getState(), event.getWorld(), entity.getBlockPos(), null);
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

        if (entity instanceof PlayerEntity && !entity.world.isClient && Capabilities.weapon.get(entity).storage(StorageType.greatsword).leapForce() > 0) {
            DamageSource damageSource = event.getSource();

            if (!damageSource.isExplosive() && !damageSource.isProjectile()) {
                event.setCanceled(true);
            }
        }

        if (entity instanceof PlayerEntity && !entity.world.isClient) {
            Entity source = event.getSource().getSource();
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
                Random random = entity.world.random;
                float attackDamage = storage.attribute(criticalStrikeRate) > random.nextDouble() ? 2 * event.getAmount() : event.getAmount();

                if (storage.hasSkill(Skills.nourishment)) {
                    SkillContainer leeching = storage.skill(Skills.nourishment);
                    float saturation = (1 + leeching.level()) * attackDamage / 20F;
                    int food = random.nextInt((int) Math.ceil(saturation) + 1);

                    ((PlayerEntity) entity).getHungerManager().add(food, 2 * saturation);
                }

                event.setAmount(attackDamage);
            }
        }
    }

    @SubscribeEvent
    public static void death(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        Entity attacker = event.getSource().getAttacker();

        if (attacker == null) {
            attacker = entity.getDamageTracker().getBiggestAttacker();
        } else if (attacker != entity.getDamageTracker().getBiggestAttacker()) {
            throw new RuntimeException();
        }

        if ((attacker instanceof PlayerEntity player) && !attacker.world.isClient) {
            EntityAttributeInstance attackDamageAttribute = entity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            double attackDamage = attackDamageAttribute == null ? 0 : attackDamageAttribute.getValue();
            double armor = entity.getAttributeValue(EntityAttributes.GENERIC_ARMOR);
            Entity immediateSource = event.getSource().getSource();
            SoulboundCapability capability = Capabilities.weapon.get(attacker);
            ItemStorage<?> storage;
            Text displayName;

            if (immediateSource instanceof SoulboundDaggerEntity) {
                storage = capability.storage(StorageType.dagger);
                displayName = ((SoulboundDaggerEntity) immediateSource).itemStack.getName();
            } else if (immediateSource instanceof SoulboundLightningEntity) {
                storage = capability.storage(StorageType.sword);
                displayName = ItemUtil.equippedStack(player, SoulboundItems.sword).getName();
            } else {
                storage = capability.storage();
                displayName = player.getMainHandStack().getName();
            }

            if (storage != null) {
                Configuration configuration = Configuration.instance();

                double xp = entity.getMaxHealth()
                    * attacker.world.getDifficulty().getId() * configuration.difficultyMultiplier
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
                    ((PlayerEntity) attacker).sendMessage(Translations.messageLevelUp.format(displayName, storage.datum(level)), true);
                }

                storage.sync();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void fall(LivingFallEvent event) {
        Entity fallen = event.getEntity();

        if (!fallen.world.isClient && fallen instanceof PlayerEntity player) {
            SoulboundCapability component = Capabilities.weapon.get(player);
            GreatswordStorage storage = component.storage(StorageType.greatsword);
            double leapForce = storage.leapForce();

            if (leapForce > 0) {
                if (storage.hasSkill(Skills.freezing)) {
                    double radiusXZ = Math.min(4, event.getDistance());
                    double radiusY = Math.min(2, 0.5F * event.getDistance());
                    List<Entity> nearbyEntities = player.world.getOtherEntities(player, new Box(
                        player.getX() - radiusXZ, player.getY() - radiusY, player.getZ() - radiusXZ,
                        player.getX() + radiusXZ, player.getY() + radiusY, player.getZ() + radiusXZ
                    ));

                    boolean froze = false;

                    for (Entity entity : nearbyEntities) {
                        if (entity.squaredDistanceTo(entity) <= radiusXZ * radiusXZ) {
                            storage.freeze(entity, (int) Math.min(60, 12 * event.getDistance()), 0.4F * event.getDistance());

                            froze = true;
                        }
                    }

                    if (froze) {
                        if (!nearbyEntities.isEmpty()) {
                            ServerWorld world = (ServerWorld) player.world;

                            for (double i = 0; i <= 2 * radiusXZ; i += radiusXZ / 48D) {
                                double x = radiusXZ - i;
                                double z = Math.sqrt((radiusXZ * radiusXZ - x * x));
                                int particles = 1;

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
    public static void tick(LivingEvent.LivingUpdateEvent event) {
        EntityData entityData = Capabilities.entityData.get(event.getEntity());
        entityData.tick();

        if (entityData.isFrozen()) {
            event.setCanceled(true);
        }

        if (event.getEntity() instanceof PlayerEntity player) {
            Capabilities.tool.get(player).tick();
            Capabilities.weapon.get(player).tick();
        }
    }

    @SubscribeEvent
    public static void teleport(EntityTeleportEvent event) {
        if (Capabilities.entityData.get(event.getEntity()).cannotTeleport()) {
            event.setCanceled(true);
        }
    }
}
