package soulboundarmory.entity;

import D;
import F;
import I;
import java.util.UUID;
import soulboundarmory.mixin.access.entity.LightningEntityAccess;
import soulboundarmory.component.soulbound.item.weapon.SwordStorage;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.serial.CompoundSerializable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.AreaHelper;

public class SoulboundLightningEntity extends LightningEntity implements LightningEntityAccess, CompoundSerializable {
    protected UUID caster;

    public SoulboundLightningEntity(World world, double x, double y, double z, UUID caster) {
        super(EntityType.LIGHTNING_BOLT, world);

        this.setCosmetic(true);
        this.caster = caster;
        this.setPosition(x, y, z);

        AreaHelper.func_242964_a(world, this.getBlockPos(), null).ifPresent(AreaHelper::createPortal);
    }

    public SoulboundLightningEntity(World world, Vec3d pos, UUID caster) {
        this(world, pos.x, pos.y, pos.z, caster);
    }

    @Override
    public void tick() {
        if (!this.world.isClient) {
            this.setFlag(6, this.isGlowing());
        }

        this.baseTick();

        if (this.life() == 2) {
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 15, 0.8F + this.random.nextFloat() * 0.2F);
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2, 0.5F + this.random.nextFloat() * 0.2F);
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
            if (this.world.isClient) {
                this.world.setLightningTicksLeft(2);
            } else {
                D radius = 3D;

                for (Entity entity : this.world.getOtherEntities(this, new Box(this.getX() - radius, this.getY() - radius, this.getZ() - radius, this.getX() + radius, this.getY() + 6 + radius, this.getZ() + radius))) {
                    LivingEntity caster = this.caster();
                    F attackDamage = caster instanceof PlayerEntity
                            ? (float) SwordStorage.get(caster).attributeTotal(StatisticType.attackDamage)
                            : 5;

                    if (entity != caster && entity instanceof LivingEntity) {
                        entity.onStruckByLightning((ServerWorld) this.world, this);
                        entity.setFireTicks(1);

                        if (!entity.isOnFire()) {
                            this.setFireTicks(160);
                        }

                        if (caster instanceof PlayerEntity) {
                            LivingEntity target = (LivingEntity) entity;
                            ItemStack itemStack = caster.getMainHandStack();
                            DamageSource damageSource = DamageSource.explosion(caster);
                            F attackDamageModifier = EnchantmentHelper.getAttackDamage(itemStack, target.getGroup());
                            I burnTime = 0;

                            if (attackDamage > 0 || attackDamageModifier > 0) {
                                I knockbackModifier = EnchantmentHelper.getKnockback(caster);
                                F initialHealth = target.getHealth();

                                burnTime += 4 * EnchantmentHelper.getFireAspect(caster);

                                if (!caster.isOnFire()) {
                                    burnTime += 5;
                                }

                                if (burnTime > 0 && !entity.isOnFire()) {
                                    entity.setFireTicks(20);
                                }

                                if (entity.damage(damageSource, attackDamage)) {
                                    PlayerEntity player = (PlayerEntity) caster;

                                    if (knockbackModifier > 0) {
                                        target.takeKnockback(knockbackModifier * 0.5F, MathHelper.sin(caster.yaw * 0.017453292F), -MathHelper.cos(caster.yaw * 0.017453292F));
                                    }

                                    if (attackDamageModifier > 0) {
                                        player.addCritParticles(entity);
                                    }

                                    target.setAttacker(caster);

                                    EnchantmentHelper.onUserDamaged(caster, target);

                                    F damageDealt = initialHealth - target.getHealth();

                                    if (burnTime > 0) {
                                        entity.setOnFireFor(burnTime);
                                    }

                                    if (caster.world instanceof ServerWorld && damageDealt > 2) {
                                        I particles = (int) (damageDealt * 0.5);

                                        ((ServerWorld) caster.world).spawnParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(), entity.getY() + entity.getHeight() * 0.5, entity.getZ(), particles, 0.1, 0, 0.1, 0.2);
                                    }
                                }
                            }
                        } else {
                            entity.damage(DamageSource.explosion(caster), attackDamage);
                        }

                        entity.onStruckByLightning((ServerWorld) this.world, this);
                    }
                }
            }
        }
    }

    public LivingEntity caster() {
        return this.world.getPlayerByUuid(this.caster);
    }

    public void caster(LivingEntity caster) {
        this.caster = caster.getUuid();
    }

    @Override
    public NbtCompound serializeNBT() {
        NbtCompound tag = super.serializeNBT();
        tag.putUuid("casterUUID", this.caster);

        return tag;
    }

    @Override
    public void deserializeNBT(NbtCompound tag) {
        super.deserializeNBT(tag);

        this.caster = tag.getUuid("casterUUID");
    }
}
