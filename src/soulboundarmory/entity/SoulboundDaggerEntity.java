package soulboundarmory.entity;

import soulboundarmory.SoulboundArmory;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.StorageType;
import soulboundarmory.component.soulbound.item.weapon.DaggerStorage;
import soulboundarmory.component.soulbound.player.SoulboundItemUtil;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.registry.Skills;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SoulboundDaggerEntity extends ExtendedProjectile {
    public ItemStack itemStack;

    protected boolean spawnClone;
    protected boolean isClone;
    protected double attackDamageRatio;
    protected int ticksToSeek;
    protected int ticksSeeking;
    protected int ticksInGround;
    protected DaggerStorage storage;
    public static final EntityType<SoulboundDaggerEntity> type = EntityType.Builder
        .create((EntityType.IFactory<SoulboundDaggerEntity>) SoulboundDaggerEntity::new, EntityClassification.MISC)
        .size(1, 1)
        .build(SoulboundArmory.id("dagger").toString());

    public SoulboundDaggerEntity(SoulboundDaggerEntity original, boolean spawnClone) {
        this(original.world, original.getEntity(), original.itemStack, spawnClone, original.getVelocityD(), original.getVelocityD());

        this.isClone = true;
        this.setPosition(original.getPosX(), original.getPosY(), original.getPosZ());
        this.setMotion(original.getMotion());
        this.setRotation(original.rotationPitch, original.rotationYaw);
        this.prevRotationPitch = original.prevRotationPitch;
        this.prevRotationYaw = original.prevRotationYaw;
        this.attackDamageRatio = original.attackDamageRatio;
    }

    public SoulboundDaggerEntity(World world, Entity shooter, ItemStack itemStack, boolean spawnClone, double velocity, double maxVelocity) {
        super(type, shooter.getPosX(), shooter.getEyeHeight() - 0.1, shooter.getPosZ(), world);

        this.ticksToSeek = -1;

        if (shooter instanceof PlayerEntity) {
            this.pickupStatus = PickupStatus.ALLOWED;
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
        this.setDamage(this.storage.attribute(StatisticType.attackDamage) * this.attackDamageRatio);
    }

    protected void updateShooter() {
        var owner = this.getEntity();

        if (owner instanceof PlayerEntity) {
            this.storage = Components.weapon.of(owner).storage(StorageType.dagger);
        }
    }

    @Override
    public void tick() {
        if (!this.tryReturn()) {
            super.tick();
        }
    }

    @Override
    protected void arrowHit(LivingEntity target) {
        var owner = this.getEntity();

        if (this.spawnClone) {
            this.world.addEntity(new SoulboundDaggerEntity(this, false));
        }

        if (!this.isClone && this.ticksSeeking == 0) {
            this.setMotion(this.getMotion().mul(-0.1, 1, 1));
            this.rotationYaw += 180;
            this.prevRotationYaw += 180;
        }

        if (owner instanceof PlayerEntity player) {
            var damageSource = DamageSource.causeArrowDamage(this, owner);
            var attackDamage = this.storage.attribute(StatisticType.attackDamage);
            var burnTime = 0;

            if (attackDamage > 0) {
                var knockbackModifier = EnchantmentHelper.getKnockbackModifier(player);
                var initialHealth = target.getHealth();

                burnTime += EnchantmentHelper.getFireAspectModifier(player) * 80;

                if (this.isBurning()) {
                    burnTime += 100;
                }

                if (burnTime > 0 && !target.isBurning()) {
                    target.forceFireTicks(1);
                }

                var velocity = target.getMotion();
                var dx = velocity.x;
                var dy = velocity.y;
                var dz = velocity.z;

                if (target.attackEntityFrom(damageSource, (float) attackDamage)) {
                    if (knockbackModifier > 0) {
                        target.applyKnockback(knockbackModifier * 0.5F, MathHelper.sin(player.rotationYaw * 0.017453292F), -MathHelper.cos(player.rotationYaw * 0.017453292F));
                    }
                }

/*
                if (!target.world.isRemote && target.attackEntityFromMarked) {
                    ((ServerPlayerEntity) target).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
                    target.attackEntityFromMarked = false;
                    this.setVelocity(dx, dy, dz);
                }
*/

                if (attackDamage > 0) {
                    player.onCriticalHit(target);
                }

                player.setRevengeTarget(target);

                EnchantmentHelper.applyThornEnchantments(target, player);

                var damageDealt = initialHealth - target.getHealth();
                player.addStat(Stats.DAMAGE_DEALT, Math.round(damageDealt * 10));

                if (burnTime > 0) {
                    target.forceFireTicks(burnTime);
                }

                if (!player.world.isRemote && damageDealt > 2) {
                    ((ServerWorld) player.world).spawnParticle(ParticleTypes.DAMAGE_INDICATOR, target.getPosX(), target.getPosY() + target.getHeight() * 0.5, target.getPosZ(), (int) (damageDealt * 0.5), 0.1, 0, 0.1, 0.2);
                }
            }
        }
    }

    @Override
    public void onCollideWithPlayer(PlayerEntity player) {
        if (!this.world.isRemote
            && player == this.getEntity()
            && (this.isClone
            || this.storage != null
            && this.life() >= Math.max(1, 20 / this.storage.attribute(StatisticType.attackSpeed))
            && (player.isCreative()
            && (SoulboundItemUtil.hasSoulWeapon(player)))
            || SoulboundItemUtil.addItemStack(this.getArrowStack(), player, true))
        ) {
            player.onItemPickup(this, 1);
            this.remove();
        }
    }

/*
    @Override
    protected void onHit(HitResult result) {
         Vector3d pos = result.getPos();
         BlockPos blockPos = new BlockPos(pos);
         BlockState blockState = this.world.getBlockState(blockPos);

        if (this.ticksSeeking == 0) {
            super.onHit(result);
        }

        if (this.world.isRemote) {
            //noinspection MethodCallSideOnly
            this.playSound(blockState.getBlock().getSoundGroup(blockState).getHitSound(), 1, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        }
    }
*/

    @Override
    protected ItemStack getArrowStack() {
        return this.storage == null ? ItemStack.EMPTY : this.storage.stack();
    }

    protected boolean tryReturn() {
        var owner = this.getEntity();

        if (owner instanceof PlayerEntity) {
            var attackSpeed = this.storage.attribute(StatisticType.attackSpeed);

            if (this.storage.hasSkill(Skills.sneakReturn) && owner.isSneaking()
                && this.life() >= 60 / attackSpeed
                || this.storage.hasSkill(Skills.returning)
                && (this.life() >= 300
                || this.ticksInGround > 20 / attackSpeed
                || owner.getDistance(this) >= 16 * (this.world.getServer().getPlayerList().getViewDistance() - 1)
                || this.getPosY() <= 0)
                || this.ticksSeeking > 0
            ) {
                var box = owner.getBoundingBox();
                var dX = (box.getMax(Direction.Axis.X) + box.getMin(Direction.Axis.X)) / 2 - this.getPosZ();
                var dY = (box.getMax(Direction.Axis.Y) + box.getMin(Direction.Axis.Y)) / 2 - this.getPosY();
                var dZ = (box.getMax(Direction.Axis.Z) + box.getMin(Direction.Axis.Z)) / 2 - this.getPosZ();
                var multiplier = 1.8 / attackSpeed;

                if (this.ticksToSeek == -1 && !this.inGround) {
                    this.ticksToSeek = (int) Math.round(Math.log(0.05) / Math.log(multiplier));
                }

                if (this.ticksToSeek > 0 && !this.inGround) {
                    this.setMotion(this.velocityX() * multiplier, this.velocityY() * multiplier, this.velocityZ() * multiplier);

                    this.ticksToSeek--;
                } else {
                    var normalized = new Vector3d(dX, dY, dZ).normalize();

                    if (this.ticksToSeek != 0) {
                        this.ticksToSeek = 0;
                    }

                    if (this.inGround) {
                        this.setMotion(0, 0, 0);
                        this.inGround = false;
                    }

                    if (this.ticksSeeking > 5) {
                        multiplier = Math.min(0.08 * this.ticksSeeking, 5);

                        if (Math.sqrt(dX * dX + dY * dY + dZ * dZ) <= 5 && this.ticksSeeking > 100) {
                            this.setMotion(dX, dY, dZ);
                        } else {
                            this.setMotion(normalized.x * multiplier, normalized.y * multiplier, normalized.z * multiplier);
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

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        super.deserializeNBT(tag);

        this.itemStack = ItemStack.read(tag.getCompound("itemStack"));

        this.updateShooter();
    }

    @Override
    public CompoundNBT serializeNBT() {
        var tag = super.serializeNBT();
        tag.put("itemStack", this.itemStack.serializeNBT());

        this.updateShooter();

        return tag;
    }
}
