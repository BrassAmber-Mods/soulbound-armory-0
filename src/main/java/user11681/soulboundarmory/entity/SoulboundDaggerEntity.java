package user11681.soulboundarmory.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.capability.soulbound.item.weapon.DaggerStorage;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundItemUtil;
import user11681.soulboundarmory.capability.statistics.StatisticType;
import user11681.soulboundarmory.registry.Skills;

import static user11681.soulboundarmory.capability.statistics.StatisticType.attackDamage;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attackSpeed;

public class SoulboundDaggerEntity extends ExtendedProjectile {
    public ItemStack itemStack;

    protected boolean spawnClone;
    protected boolean isClone;
    protected double attackDamageRatio;
    protected int ticksToSeek;
    protected int ticksSeeking;
    protected int ticksInGround;
    protected DaggerStorage storage;
    public static final EntityType<SoulboundDaggerEntity> type = Registry
            .register(Registry.ENTITY_TYPE, new ResourceLocation(SoulboundArmory.ID, "dagger"), FabricEntityTypeBuilder
                    .create(SpawnGroup.MISC, (EntityType.EntityFactory<SoulboundDaggerEntity>) SoulboundDaggerEntity::new)
                    .dimensions(EntityDimensions.fixed(1, 1)).build());

    public SoulboundDaggerEntity(SoulboundDaggerEntity original, boolean spawnClone) {
        this(original.level, original.getOwner(), original.itemStack, spawnClone, original.getDeltaMovementD(), original.getDeltaMovementD());

        this.isClone = true;
        this.setPos(original.getX(), original.getY(), original.getZ());
        this.setDeltaMovement(original.getDeltaMovement());
        this.setRot(original.xRot, original.yRot);
        this.xRotO = original.xRotO;
        this.yRotO = original.yRotO;
        this.attackDamageRatio = original.attackDamageRatio;
    }

    public SoulboundDaggerEntity(World world, Entity shooter, ItemStack itemStack, boolean spawnClone, double velocity, double maxVelocity) {
        super(type, shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ(), world);

        this.ticksToSeek = -1;

        if (shooter instanceof PlayerEntity) {
            this.pickup = PickupStatus.ALLOWED;
        }

        this.itemStack = itemStack;
        this.spawnClone = spawnClone;

        this.attackDamageRatio = velocity / maxVelocity;

        this.updateShooter();
        this.setDamage();
    }

    public <T extends SoulboundDaggerEntity> SoulboundDaggerEntity(EntityType<T> entityType, World world) {
        super(entityType, world);
    }

    public void setDamage() {
        this.setBaseDamage(this.storage.getAttribute(attackDamage) * this.attackDamageRatio);
    }

    @Override
    protected ItemStack getPickupItem() {
        return this.storage == null ? ItemStack.EMPTY : this.storage.getItemStack();
    }

    protected void updateShooter() {
        final Entity owner = this.getOwner();

        if (owner instanceof PlayerEntity) {
            this.storage = Capabilities.weapon.get(owner).getStorage(StorageType.dagger);
        }
    }

    @Override
    public void tick() {
        if (!this.tryReturn()) {
            super.tick();
        }
    }

    protected boolean tryReturn() {
        final Entity owner = this.getOwner();

        if (owner instanceof PlayerEntity) {
            final double attackSpeed = this.storage.getAttribute(StatisticType.attackSpeed);

            if (this.storage.hasSkill(Skills.sneakReturn) && owner.isShiftKeyDown()
                    && this.getLife() >= 60 / attackSpeed
                    || this.storage.hasSkill(Skills.returning)
                    && (this.getLife() >= 300
                    || this.ticksInGround > 20 / attackSpeed
                    || owner.distanceTo(this) >= 16 * (this.level.getServer().getPlayerList().getViewDistance() - 1)
                    || this.getY() <= 0)
                    || this.ticksSeeking > 0) {
                AxisAlignedBB box = owner.getBoundingBox();
                double dX = (box.max(Direction.Axis.X) + box.min(Direction.Axis.X)) / 2 - this.getZ();
                double dY = (box.max(Direction.Axis.Y) + box.min(Direction.Axis.Y)) / 2 - this.getY();
                double dZ = (box.max(Direction.Axis.Z) + box.min(Direction.Axis.Z)) / 2 - this.getZ();
                double multiplier = 1.8 / attackSpeed;

                if (this.ticksToSeek == -1 && !this.inGround) {
                    this.ticksToSeek = (int) Math.round(Math.log(0.05) / Math.log(multiplier));
                }

                if (this.ticksToSeek > 0 && !this.inGround) {
                    this.setDeltaMovement(this.velocityX() * multiplier, this.velocityY() * multiplier, this.velocityZ() * multiplier);

                    this.ticksToSeek--;
                } else {
                    Vector3d normalized = new Vector3d(dX, dY, dZ).normalize();

                    if (this.ticksToSeek != 0) {
                        this.ticksToSeek = 0;
                    }

                    if (this.inGround) {
                        this.setDeltaMovement(0, 0, 0);
                        this.inGround = false;
                    }

                    if (this.ticksSeeking > 5) {
                        multiplier = Math.min(0.08 * this.ticksSeeking, 5);

                        if (Math.sqrt(dX * dX + dY * dY + dZ * dZ) <= 5 && this.ticksSeeking > 100) {
                            this.setDeltaMovement(dX, dY, dZ);
                        } else {
                            this.setDeltaMovement(normalized.x * multiplier, normalized.y * multiplier, normalized.z * multiplier);
                        }
                    } else {
                        multiplier = 0.08;
                        this.addVelocity(normalized.x * multiplier, normalized.y * multiplier, normalized.z * multiplier);
                    }

                    this.ticksSeeking++;

                    return true;
                }
            }
        }

        return false;
    }

/*
    @Override
    protected void onHit(RayTraceResult result) {
        final Vector3d pos = result.getPos();
        final BlockPos blockPos = new BlockPos(pos);
        final BlockState blockState = this.level.getBlockState(blockPos);

        if (this.ticksSeeking == 0) {
            super.onHit(result);
        }

        if (this.level.isClientSide) {
            //noinspection MethodCallSideOnly
            this.playSound(blockState.getBlock().getSoundGroup(blockState).getHitSound(), 1, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        }
    }
*/

    @Override
    protected void doPostHurtEffects(LivingEntity target) {
        Entity owner = this.getOwner();

        if (this.spawnClone) {
            this.level.addFreshEntity(new SoulboundDaggerEntity(this, false));
        }

        if (!this.isClone && this.ticksSeeking == 0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.1, 1, 1));
            this.yRot += 180;
            this.yRotO += 180;
        }

        if (owner instanceof PlayerEntity player) {
            DamageSource damageSource = DamageSource.arrow(this, owner);
            double attackDamage = this.storage.getAttribute(StatisticType.attackDamage);

            int burnTime = 0;

            if (attackDamage > 0) {
                int knockbackModifier = EnchantmentHelper.getKnockbackBonus(player);
                float initialHealth = target.getHealth();

                burnTime += EnchantmentHelper.getFireAspect(player) * 80;

                if (this.isOnFire()) {
                    burnTime += 100;
                }

                if (burnTime > 0 && !target.isOnFire()) {
                    target.setRemainingFireTicks(1);
                }

                Vector3d velocity = target.getDeltaMovement();
                double velocityX = velocity.x;
                double velocityY = velocity.y;
                double velocityZ = velocity.z;

                if (target.hurt(damageSource, (float) attackDamage)) {
                    if (knockbackModifier > 0) {
                        target.knockback(knockbackModifier * 0.5F, MathHelper.sin(player.yRot * 0.017453292F), -MathHelper.cos(player.yRot * 0.017453292F));
                    }
                }

                if (!target.level.isClientSide && target.velocityModified) {
                    ((ServerPlayerEntity) target).connection.send(new SEntityVelocityPacket(target));
                    target.velocityModified = false;
                    this.setDeltaMovement(velocityX, velocityY, velocityZ);
                }

                if (attackDamage > 0) {
                    player.crit(target);
                }

                player.setLastHurtByMob(target);

                EnchantmentHelper.doPostDamageEffects(target, player);

                float damageDealt = initialHealth - target.getHealth();
                player.awardStat(Stats.DAMAGE_DEALT, Math.round(damageDealt * 10));

                if (burnTime > 0) {
                    target.setRemainingFireTicks(burnTime);
                }

                if (!player.level.isClientSide && damageDealt > 2) {
                    ((ServerWorld) player.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(), (int) (damageDealt * 0.5), 0.1, 0, 0.1, 0.2);
                }
            }
        }
    }

    @Override
    public void playerTouch(PlayerEntity player) {
        if (!this.level.isClientSide
                && player == this.getOwner()
                && (this.isClone
                || this.storage != null
                && this.getLife() >= Math.max(1, 20 / this.storage.getAttribute(attackSpeed))
                && (player.isCreative()
                && (SoulboundItemUtil.hasSoulWeapon(player)))
                || SoulboundItemUtil.addItemStack(this.getPickupItem(), player, true))) {
            player.take(this, 1);
            this.remove();
        }
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        super.deserializeNBT(tag);

        this.itemStack = ItemStack.of(tag.getCompound("itemStack"));

        this.updateShooter();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = super.serializeNBT();
        tag.put("itemStack", this.itemStack.serializeNBT());

        this.updateShooter();

        return tag;
    }
}
