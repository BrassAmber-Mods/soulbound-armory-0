package user11681.soulboundarmory.entity;

import java.util.UUID;
import net.minecraft.block.PortalSize;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import user11681.soulboundarmory.asm.access.entity.LightningEntityAccess;
import user11681.soulboundarmory.capability.soulbound.item.weapon.SwordStorage;
import user11681.soulboundarmory.capability.statistics.StatisticType;
import user11681.soulboundarmory.serial.CompoundSerializable;

public class SoulboundLightningEntity extends LightningBoltEntity implements LightningEntityAccess, CompoundSerializable {
    protected UUID caster;

    public SoulboundLightningEntity(World world, double x, double y, double z, UUID caster) {
        super(EntityType.LIGHTNING_BOLT, world);

        this.setVisualOnly(true);
        this.caster = caster;
        this.setPos(x, y, z);

        PortalSize.findEmptyPortalShape(world, this.blockPosition(), null).ifPresent(PortalSize::createPortalBlocks);
    }

    public SoulboundLightningEntity(World world, final Vector3d pos, final UUID caster) {
        this(world, pos.x, pos.y, pos.z, caster);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            this.setSharedFlag(6, this.isGlowing());
        }

        this.baseTick();

        if (this.life() == 2) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 15, 0.8F + this.random.nextFloat() * 0.2F);
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2, 0.5F + this.random.nextFloat() * 0.2F);
        }

        this.life(this.life() - 1);

        if (this.life() < 0) {
            if (this.flashes() == 0) {
                this.remove();
            } else if (this.life() < -this.random.nextInt(10)) {
                this.flashes(this.flashes() - 1);
                this.life(1);
                this.seed = this.random.nextLong();
            }
        }

        if (this.life() >= 0) {
            if (this.level.isClientSide) {
                this.level.setSkyFlashTime(2);
            } else {
                double radius = 3;

                for (Entity entity : this.level.getEntities(this,
                        new AxisAlignedBB(this.getX() - radius, this.getY() - radius, this.getZ() - radius, this.getX() + radius, this.getY() + 6 + radius, this.getZ() + radius))) {
                    LivingEntity caster = this.caster();
                    float attackDamage = caster instanceof PlayerEntity
                            ? (float) SwordStorage.get(caster).getAttributeTotal(StatisticType.attackDamage)
                            : 5;

                    if (entity != caster && entity instanceof LivingEntity) {
                        entity.thunderHit((ServerWorld) this.level, this);
                        entity.setRemainingFireTicks(1);

                        if (!entity.isOnFire()) {
                            this.setRemainingFireTicks(160);
                        }

                        if (caster instanceof PlayerEntity) {
                            LivingEntity target = (LivingEntity) entity;
                            ItemStack itemStack = caster.getMainHandItem();
                            DamageSource damageSource = DamageSource.explosion(caster);
                            float attackDamageModifier = EnchantmentHelper.getDamageBonus(itemStack, target.getMobType());
                            int burnTime = 0;

                            if (attackDamage > 0 || attackDamageModifier > 0) {
                                int knockbackModifier = EnchantmentHelper.getKnockbackBonus(caster);
                                float initialHealth = target.getHealth();

                                burnTime += 4 * EnchantmentHelper.getFireAspect(caster);

                                if (!caster.isOnFire()) {
                                    burnTime += 5;
                                }

                                if (burnTime > 0 && !entity.isOnFire()) {
                                    entity.setRemainingFireTicks(20);
                                }

                                if (entity.hurt(damageSource, attackDamage)) {
                                    final PlayerEntity player = (PlayerEntity) caster;

                                    if (knockbackModifier > 0) {
                                        target.knockback(knockbackModifier * 0.5F, MathHelper.sin(caster.yRot * 0.017453292F), -MathHelper.cos(caster.yRot * 0.017453292F));
                                    }

                                    if (attackDamageModifier > 0) {
                                        player.crit(entity);
                                    }

                                    ((LivingEntity) entity).setLastHurtByMob(caster);

                                    EnchantmentHelper.doPostDamageEffects(caster, target);

                                    float damageDealt = initialHealth - target.getHealth();

                                    if (burnTime > 0) {
                                        entity.setSecondsOnFire(burnTime);
                                    }

                                    if (caster.level instanceof ServerWorld && damageDealt > 2) {
                                        int particles = (int) (damageDealt * 0.5);

                                        ((ServerWorld) caster.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ(), particles, 0.1, 0, 0.1, 0.2);
                                    }
                                }
                            }
                        } else {
                            entity.hurt(DamageSource.explosion(caster), attackDamage);
                        }

                        entity.thunderHit((ServerWorld) this.level, this);
                    }
                }
            }
        }
    }

    public LivingEntity caster() {
        return this.level.getPlayerByUUID(this.caster);
    }

    public void caster(LivingEntity caster) {
        this.caster = caster.getUUID();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = super.serializeNBT();
        tag.putUUID("casterUUID", this.caster);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        super.deserializeNBT(tag);

        this.caster = tag.getUUID("casterUUID");
    }
}
