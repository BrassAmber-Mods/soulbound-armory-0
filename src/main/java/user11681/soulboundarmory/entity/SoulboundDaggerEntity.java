package user11681.soulboundarmory.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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
    public static final EntityType<SoulboundDaggerEntity> type = EntityType.Builder
        .create((EntityType.EntityFactory<SoulboundDaggerEntity>) SoulboundDaggerEntity::new, SpawnGroup.MISC)
        .setDimensions(1, 1).build(SoulboundArmory.id("dagger").toString());

    public SoulboundDaggerEntity(SoulboundDaggerEntity original, boolean spawnClone) {
        this(original.world, original.getOwner(), original.itemStack, spawnClone, original.getVelocityD(), original.getVelocityD());

        this.isClone = true;
        this.setPos(original.getX(), original.getY(), original.getZ());
        this.setVelocity(original.getVelocity());
        this.setRotation(original.pitch, original.yaw);
        this.prevPitch = original.prevPitch;
        this.prevYaw = original.prevYaw;
        this.attackDamageRatio = original.attackDamageRatio;
    }

    public SoulboundDaggerEntity(World world, Entity shooter, ItemStack itemStack, boolean spawnClone, double velocity, double maxVelocity) {
        super(type, shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ(), world);

        this.ticksToSeek = -1;

        if (shooter instanceof PlayerEntity) {
            this.pickupType = PickupPermission.ALLOWED;
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
        this.setDamage(this.storage.attribute(attackDamage) * this.attackDamageRatio);
    }

    protected void updateShooter() {
        Entity owner = this.getOwner();

        if (owner instanceof PlayerEntity) {
            this.storage = Capabilities.weapon.get(owner).storage(StorageType.dagger);
        }
    }

    @Override
    public void tick() {
        if (!this.tryReturn()) {
            super.tick();
        }
    }

    @Override
    protected void onHit(LivingEntity target) {
        Entity owner = this.getOwner();

        if (this.spawnClone) {
            this.world.spawnEntity(new SoulboundDaggerEntity(this, false));
        }

        if (!this.isClone && this.ticksSeeking == 0) {
            this.setVelocity(this.getVelocity().multiply(-0.1, 1, 1));
            this.yaw += 180;
            this.prevYaw += 180;
        }

        if (owner instanceof PlayerEntity player) {
            DamageSource damageSource = DamageSource.arrow(this, owner);
            double attackDamage = this.storage.attribute(StatisticType.attackDamage);
            int burnTime = 0;

            if (attackDamage > 0) {
                int knockbackModifier = EnchantmentHelper.getKnockback(player);
                float initialHealth = target.getHealth();

                burnTime += EnchantmentHelper.getFireAspect(player) * 80;

                if (this.isOnFire()) {
                    burnTime += 100;
                }

                if (burnTime > 0 && !target.isOnFire()) {
                    target.setFireTicks(1);
                }

                Vec3d velocity = target.getVelocity();
                double velocityX = velocity.x;
                double velocityY = velocity.y;
                double velocityZ = velocity.z;

                if (target.damage(damageSource, (float) attackDamage)) {
                    if (knockbackModifier > 0) {
                        target.takeKnockback(knockbackModifier * 0.5F, MathHelper.sin(player.yaw * 0.017453292F), -MathHelper.cos(player.yaw * 0.017453292F));
                    }
                }

/*
                if (!target.world.isClient && target.hurtMarked) {
                    ((ServerPlayerEntity) target).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
                    target.hurtMarked = false;
                    this.setVelocity(velocityX, velocityY, velocityZ);
                }
*/

                if (attackDamage > 0) {
                    player.addCritParticles(target);
                }

                player.setAttacker(target);

                EnchantmentHelper.onTargetDamaged(target, player);

                float damageDealt = initialHealth - target.getHealth();
                player.increaseStat(Stats.DAMAGE_DEALT, Math.round(damageDealt * 10));

                if (burnTime > 0) {
                    target.setFireTicks(burnTime);
                }

                if (!player.world.isClient && damageDealt > 2) {
                    ((ServerWorld) player.world).spawnParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY() + target.getHeight() * 0.5, target.getZ(), (int) (damageDealt * 0.5), 0.1, 0, 0.1, 0.2);
                }
            }
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (!this.world.isClient
            && player == this.getOwner()
            && (this.isClone
            || this.storage != null
            && this.getLife() >= Math.max(1, 20 / this.storage.attribute(attackSpeed))
            && (player.isCreative()
            && (SoulboundItemUtil.hasSoulWeapon(player)))
            || SoulboundItemUtil.addItemStack(this.asItemStack(), player, true))) {
            player.sendPickup(this, 1);
            this.remove();
        }
    }

/*
    @Override
    protected void onHit(HitResult result) {
         Vec3d pos = result.getPos();
         BlockPos blockPos = new BlockPos(pos);
         BlockState blockState = this.world.getBlockState(blockPos);

        if (this.ticksSeeking == 0) {
            super.onHit(result);
        }

        if (this.world.isClient) {
            //noinspection MethodCallSideOnly
            this.playSound(blockState.getBlock().getSoundGroup(blockState).getHitSound(), 1, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        }
    }
*/

    @Override
    protected ItemStack asItemStack() {
        return this.storage == null ? ItemStack.EMPTY : this.storage.itemStack();
    }

    protected boolean tryReturn() {
        Entity owner = this.getOwner();

        if (owner instanceof PlayerEntity) {
            double attackSpeed = this.storage.attribute(StatisticType.attackSpeed);

            if (this.storage.hasSkill(Skills.sneakReturn) && owner.isSneaking()
                && this.getLife() >= 60 / attackSpeed
                || this.storage.hasSkill(Skills.returning)
                && (this.getLife() >= 300
                || this.ticksInGround > 20 / attackSpeed
                || owner.distanceTo(this) >= 16 * (this.world.getServer().getPlayerManager().getViewDistance() - 1)
                || this.getY() <= 0)
                || this.ticksSeeking > 0) {
                Box box = owner.getBoundingBox();
                double dX = (box.getMax(Direction.Axis.X) + box.getMin(Direction.Axis.X)) / 2 - this.getZ();
                double dY = (box.getMax(Direction.Axis.Y) + box.getMin(Direction.Axis.Y)) / 2 - this.getY();
                double dZ = (box.getMax(Direction.Axis.Z) + box.getMin(Direction.Axis.Z)) / 2 - this.getZ();
                double multiplier = 1.8 / attackSpeed;

                if (this.ticksToSeek == -1 && !this.inGround) {
                    this.ticksToSeek = (int) Math.round(Math.log(0.05) / Math.log(multiplier));
                }

                if (this.ticksToSeek > 0 && !this.inGround) {
                    this.setVelocity(this.velocityX() * multiplier, this.velocityY() * multiplier, this.velocityZ() * multiplier);

                    this.ticksToSeek--;
                } else {
                    Vec3d normalized = new Vec3d(dX, dY, dZ).normalize();

                    if (this.ticksToSeek != 0) {
                        this.ticksToSeek = 0;
                    }

                    if (this.inGround) {
                        this.setVelocity(0, 0, 0);
                        this.inGround = false;
                    }

                    if (this.ticksSeeking > 5) {
                        multiplier = Math.min(0.08 * this.ticksSeeking, 5);

                        if (Math.sqrt(dX * dX + dY * dY + dZ * dZ) <= 5 && this.ticksSeeking > 100) {
                            this.setVelocity(dX, dY, dZ);
                        } else {
                            this.setVelocity(normalized.x * multiplier, normalized.y * multiplier, normalized.z * multiplier);
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
    public void deserializeNBT(NbtCompound tag) {
        super.deserializeNBT(tag);

        this.itemStack = ItemStack.fromNbt(tag.getCompound("itemStack"));

        this.updateShooter();
    }

    @Override
    public NbtCompound serializeNBT() {
        NbtCompound tag = super.serializeNBT();
        tag.put("itemStack", this.itemStack.serializeNBT());

        this.updateShooter();

        return tag;
    }
}
