package user11681.soulboundarmory.entity;

import java.util.UUID;
import net.minecraft.AreaHelper;
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
import user11681.soulboundarmory.component.soulbound.item.weapon.SwordStorage;
import user11681.soulboundarmory.asm.access.entity.LightningEntityAccess;
import user11681.soulboundarmory.component.statistics.StatisticType;

@SuppressWarnings("EntityConstructor")
public class SoulboundLightningEntity extends LightningEntity implements LightningEntityAccess {
    protected UUID casterUUID;

    public SoulboundLightningEntity(final World world, final double x, final double y, final double z, final UUID casterUUID) {
        super(EntityType.LIGHTNING_BOLT, world);

        this.method_29498(true);
        this.casterUUID = casterUUID;
        this.setPos(x, y, z);

        AreaHelper.method_30485(world, this.getBlockPos(), null).ifPresent(AreaHelper::createPortal);
    }

    public SoulboundLightningEntity(final World world, final Vec3d pos, final UUID casterUUID) {
        this(world, pos.x, pos.y, pos.z, casterUUID);
    }

    @Override
    public void tick() {
        if (!this.world.isClient) {
            this.setFlag(6, this.isGlowing());
        }

        this.baseTick();

        if (this.getAmbientTick() == 2) {
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 15, 0.8F + this.random.nextFloat() * 0.2F);
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2, 0.5F + this.random.nextFloat() * 0.2F);
        }

        this.setAmbientTick(this.getAmbientTick() - 1);

        if (this.getAmbientTick() < 0) {
            if (this.getRemainingActions() == 0) {
                this.remove();
            } else if (this.getAmbientTick() < -this.random.nextInt(10)) {
                this.setRemainingActions(this.getRemainingActions() - 1);
                this.setAmbientTick(1);
                this.seed = this.random.nextLong();
            }
        }

        if (this.getAmbientTick() >= 0) {
            if (this.world.isClient) {
                this.world.setLightningTicksLeft(2);
            } else {
                final double radius = 3;

                for (final Entity entity : this.world.getEntities(this,
                        new Box(this.getX() - radius, this.getY() - radius, this.getZ() - radius, this.getX() + radius, this.getY() + 6 + radius, this.getZ() + radius))) {
                    final LivingEntity caster = this.getCaster();
                    final float attackDamage = caster instanceof PlayerEntity
                            ? (float) SwordStorage.get(caster).getAttributeTotal(StatisticType.attackDamage)
                            : 5;

                    if (entity != caster && entity instanceof LivingEntity) {
                        entity.onStruckByLightning((ServerWorld) this.world, this);
                        entity.setFireTicks(1);

                        if (!entity.isOnFire()) {
                            this.setFireTicks(160);
                        }

                        if (caster instanceof PlayerEntity) {
                            final LivingEntity target = (LivingEntity) entity;
                            final ItemStack itemStack = caster.getMainHandStack();
                            final DamageSource damageSource = DamageSource.explosion(caster);
                            final float attackDamageModifier = EnchantmentHelper.getAttackDamage(itemStack, target.getGroup());
                            int burnTime = 0;

                            if (attackDamage > 0 || attackDamageModifier > 0) {
                                final int knockbackModifier = EnchantmentHelper.getKnockback(caster);
                                final float initialHealth = target.getHealth();

                                burnTime += 4 * EnchantmentHelper.getFireAspect(caster);

                                if (!caster.isOnFire()) {
                                    burnTime += 5;
                                }

                                if (burnTime > 0 && !entity.isOnFire()) {
                                    entity.setFireTicks(20);
                                }

                                if (entity.damage(damageSource, attackDamage)) {
                                    final PlayerEntity player = (PlayerEntity) caster;

                                    if (knockbackModifier > 0) {
                                        target.takeKnockback(knockbackModifier * 0.5F, MathHelper.sin(caster.yaw * 0.017453292F), -MathHelper.cos(caster.yaw * 0.017453292F));
                                    }

                                    if (attackDamageModifier > 0) {
                                        player.addCritParticles(entity);
                                    }

                                    ((LivingEntity) entity).setAttacker(caster);

                                    EnchantmentHelper.onTargetDamaged(caster, target);

                                    final float damageDealt = initialHealth - target.getHealth();

                                    if (burnTime > 0) {
                                        entity.setOnFireFor(burnTime);
                                    }

                                    if (caster.world instanceof ServerWorld && damageDealt > 2) {
                                        final int particles = (int) (damageDealt * 0.5);

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

    @Override
    public NbtCompound toTag(NbtCompound compound) {
        compound = super.toTag(compound);
        compound.putUuid("casterUUID", this.casterUUID);

        return compound;
    }

    @Override
    public void fromTag(final NbtCompound compound) {
        super.fromTag(compound);

        this.casterUUID = compound.getUuid("casterUUID");
    }

    public LivingEntity getCaster() {
        return this.world.getPlayerByUuid(this.casterUUID);
    }

    public void setCaster(final LivingEntity caster) {
        this.casterUUID = caster.getUuid();
    }
}
