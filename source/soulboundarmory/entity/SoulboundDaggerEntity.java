package soulboundarmory.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.item.weapon.DaggerComponent;
import soulboundarmory.registry.Skills;
import soulboundarmory.util.Util;

public class SoulboundDaggerEntity extends ExtendedProjectile {
    public static final EntityType<SoulboundDaggerEntity> type = EntityType.Builder
        .create((EntityType.EntityFactory<SoulboundDaggerEntity>) SoulboundDaggerEntity::new, SpawnGroup.MISC)
        .setDimensions(1, 1)
        .build(Util.id("dagger").toString());

    public ItemStack itemStack;

    protected boolean spawnClone;
    protected boolean clone;
    protected double attackDamageRatio;
    protected int ticksToSeek = -1;
    protected int ticksSeeking;
    protected int ticksInGround;

    public SoulboundDaggerEntity(World world, LivingEntity shooter, ItemStack itemStack, boolean spawnClone, double speed, double maxSpeed) {
        super(type, shooter, world);

        if (shooter instanceof PlayerEntity) {
            this.pickupType = PickupPermission.ALLOWED;
        }

        this.itemStack = itemStack;
        this.spawnClone = spawnClone;
        this.attackDamageRatio = speed / maxSpeed;

        this.setVelocity(shooter.getRotationVector().multiply(speed));
        this.setDamage(this.component().attackDamage() * this.attackDamageRatio);
    }

    private SoulboundDaggerEntity(SoulboundDaggerEntity original) {
        this(original.world, (LivingEntity) original.getOwner(), original.itemStack, false, original.getVelocityD(), original.getVelocityD());

        this.clone = true;
        this.setPosition(original.getX(), original.getY(), original.getZ());
        this.setVelocity(original.getVelocity());
        this.setRotation(original.getPitch(), original.getYaw());
        this.prevPitch = original.prevPitch;
        this.prevYaw = original.prevYaw;
        this.attackDamageRatio = original.attackDamageRatio;
    }

    private <T extends SoulboundDaggerEntity> SoulboundDaggerEntity(EntityType<T> type, World world) {
        super(type, world);
    }

    @Override
    public byte getPierceLevel() {
        return Byte.MAX_VALUE;
    }

    @Override
    public void tick() {
        if (!this.tryReturn()) {
            super.tick();
        }
    }

    @Override
    protected void onHit(LivingEntity target) {
        if (this.spawnClone) {
            this.world.spawnEntity(new SoulboundDaggerEntity(this));
        }

        if (!this.clone && this.ticksSeeking == 0) {
            this.setVelocity(this.getVelocity().multiply(-0.1, 1, 1));
            this.setYaw(this.getYaw() + 180);
            this.prevYaw += 180;
        }

        if (this.getOwner() instanceof ServerPlayerEntity player) {
            player.attack(target);
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (!this.world.isClient && player == this.getOwner()) {
            if (this.clone || this.age >= Math.max(1, 20 / this.component().attackSpeed()) && player.getInventory().insertStack(this.asItemStack())) {
                player.sendPickup(this, 1);
                this.discard();
            }
        }
    }

    @Override
    protected void onCollision(HitResult result) {
        var pos = result.getPos();
        var blockPos = new BlockPos(pos);
        var blockState = this.world.getBlockState(blockPos);

        if (this.ticksSeeking == 0) {
            super.onCollision(result);
        }

        if (!this.world.isClient) {
            this.playSound(blockState.getBlock().getSoundGroup(blockState).getHitSound(), 1, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        }
    }

    @Override
    public void deserializeNBT(NbtCompound tag) {
        super.deserializeNBT(tag);

        this.itemStack = ItemStack.fromNbt(tag.getCompound("itemStack"));
    }

    @Override
    public NbtCompound serializeNBT() {
        var tag = super.serializeNBT();
        tag.put("itemStack", this.itemStack.serializeNBT());

        return tag;
    }

    @Override
    protected ItemStack asItemStack() {
        return this.component() == null ? ItemStack.EMPTY : this.component().stack();
    }

    protected boolean tryReturn() {
        if (this.getOwner() instanceof PlayerEntity owner) {
            var attackSpeed = this.component().attackSpeed();

            if (this.component().hasSkill(Skills.sneakReturn) && owner.isSneaking()
                && this.age >= 60 / attackSpeed
                || this.component().hasSkill(Skills.returning)
                && (this.age >= 300
                || this.ticksInGround > 20 / attackSpeed
                || owner.distanceTo(this) >= 16 * (this.world.getServer().getPlayerManager().getViewDistance() - 1)
                || this.getY() <= 0)
                || this.ticksSeeking > 0
            ) {
                var box = owner.getBoundingBox();
                var dX = (box.getMax(Direction.Axis.X) + box.getMin(Direction.Axis.X)) / 2 - this.getZ();
                var dY = (box.getMax(Direction.Axis.Y) + box.getMin(Direction.Axis.Y)) / 2 - this.getY();
                var dZ = (box.getMax(Direction.Axis.Z) + box.getMin(Direction.Axis.Z)) / 2 - this.getZ();
                var multiplier = 1.8 / attackSpeed;

                if (this.ticksToSeek == -1 && !this.inGround) {
                    this.ticksToSeek = (int) Math.round(Math.log(0.05) / Math.log(multiplier));
                }

                if (this.ticksToSeek > 0 && !this.inGround) {
                    this.setVelocity(this.velocityX() * multiplier, this.velocityY() * multiplier, this.velocityZ() * multiplier);
                    this.ticksToSeek--;
                } else {
                    var normalized = new Vec3d(dX, dY, dZ).normalize();

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

    private DaggerComponent component() {
        return ItemComponentType.dagger.of(this.getOwner());
    }
}
