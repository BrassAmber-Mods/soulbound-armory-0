package soulboundarmory.entity;

import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.item.weapon.DaggerComponent;
import soulboundarmory.registry.Skills;
import soulboundarmory.util.Util;

public class SoulboundDaggerEntity extends ExtendedProjectile {
    public static final EntityType<SoulboundDaggerEntity> type = EntityType.Builder
        .create((EntityType.EntityFactory<SoulboundDaggerEntity>) SoulboundDaggerEntity::new, SpawnGroup.MISC)
        .setDimensions(.3F, .5F)
        .build(Util.id("dagger").toString());

    public static SoulboundDaggerEntity attacker;

    public double damageRatio;

    protected final boolean clone;
    protected final boolean spawnClone;
    protected int ticksToSeek = -1;
    protected int ticksSeeking;
    protected int ticksInGround;

    public SoulboundDaggerEntity(PlayerEntity shooter, boolean clone, double speed, double damageRatio) {
        super(type, shooter, shooter.world);

        this.clone = clone;
        this.spawnClone = !clone && this.component().get().hasSkill(Skills.shadowClone);
        this.damageRatio = damageRatio;
        this.pickupType = PickupPermission.ALLOWED;

        this.setVelocity(shooter.getRotationVector().multiply(speed));
        this.setDamage(this.component().get().attackDamage() * this.damageRatio);
    }

    private SoulboundDaggerEntity(SoulboundDaggerEntity original) {
        this((PlayerEntity) original.getOwner(), true, original.velocityD(), original.damageRatio);

        this.setPosition(original.getX(), original.getY(), original.getZ());
        this.setVelocity(original.getVelocity());
        this.setRotation(original.getYaw(), original.getPitch());
        this.prevPitch = original.prevPitch;
        this.prevYaw = original.prevYaw;
    }

    private <T extends SoulboundDaggerEntity> SoulboundDaggerEntity(EntityType<T> type, World world) {
        super(type, world);

        this.clone = false;
        this.spawnClone = false;
    }

    public Optional<DaggerComponent> component() {
        return ItemComponentType.dagger.nullable(this.getOwner());
    }

    @Override
    public ItemStack asItemStack() {
        return this.component().map(ItemComponent::stack).orElse(ItemStack.EMPTY);
    }

    @Override
    public byte getPierceLevel() {
        return -1;
    }

    @Override
    public void tick() {
        if (this.isServer()) {
            top:
            if (this.component().isPresent()) {
                var component = this.component().get();
                var owner = component.player;
                var attackSpeed = component.attackSpeed();

                if (this.seeking()
                    || component.hasSkill(Skills.sneakReturn)
                    && owner.isSneaking()
                    && this.age >= 60 / attackSpeed
                    || component.hasSkill(Skills.returning)
                    && (this.age >= 300
                    || this.ticksInGround > 20 / attackSpeed
                    || owner.distanceTo(this) >= 16 * (this.world.getServer().getPlayerManager().getViewDistance() - 1)
                    || this.getY() <= 0)
                ) {
                    var box = owner.getBoundingBox();
                    var dx = (box.getMax(Direction.Axis.X) + box.getMin(Direction.Axis.X)) / 2 - this.getZ();
                    var dy = (box.getMax(Direction.Axis.Y) + box.getMin(Direction.Axis.Y)) / 2 - this.getY();
                    var dz = (box.getMax(Direction.Axis.Z) + box.getMin(Direction.Axis.Z)) / 2 - this.getZ();
                    var multiplier = 1.8 / attackSpeed;

                    if (this.ticksToSeek == -1 && !this.inGround) {
                        this.ticksToSeek = (int) Math.round(Math.log(0.05) / Math.log(multiplier));
                    }

                    if (this.ticksToSeek > 0 && !this.inGround) {
                        this.setVelocity(this.getVelocity().multiply(multiplier));
                        this.ticksToSeek--;
                    } else {
                        var normalized = new Vec3d(dx, dy, dz).normalize();

                        if (this.ticksToSeek != 0) {
                            this.ticksToSeek = 0;
                        }

                        if (this.inGround) {
                            this.setVelocity(0, 0, 0);
                            this.inGround = false;
                        }

                        if (this.ticksSeeking > 5) {
                            multiplier = Math.min(0.08 * this.ticksSeeking, 5);

                            if (MathHelper.magnitude(dx, dy, dz) <= 5 && this.ticksSeeking > 100) {
                                this.setVelocity(dx, dy, dz);
                            } else {
                                this.setVelocity(normalized.multiply(multiplier));
                            }
                        } else {
                            multiplier = 0.08;
                            this.setVelocity(this.getVelocity().add(normalized.multiply(multiplier)));
                        }

                        this.ticksSeeking++;

                        // break top;
                    }

                    this.move(MovementType.SELF, this.getVelocity());
                }

                // return;
            }
        }

        super.tick();
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (!this.world.isClient && player == this.getOwner()) {
            if (this.clone || this.age >= Math.max(1, 20 / this.component().get().attackSpeed()) && player.getInventory().insertStack(this.asItemStack())) {
                player.sendPickup(this, 1);
                this.discard();
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult target) {
        if (!this.seeking() && this.spawnClone) {
            this.world.spawnEntity(new SoulboundDaggerEntity(this));
        }

        if (this.getOwner() instanceof ServerPlayerEntity player && target.getEntity() != player) {
            attacker = this;
            player.attack(target.getEntity());
            attacker = null;

            if (!this.clone && !this.seeking()) {
                this.setVelocity(this.getVelocity().multiply(-.2, 1, -.2));
                this.setYaw(this.getYaw() + 180);
                this.prevYaw += 180;
            }
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult result) {
        if (!this.seeking()) {
            super.onBlockHit(result);

            if (this.isServer()) {
                var blockState = this.world.getBlockState(result.getBlockPos());
                this.playSound(blockState.getBlock().getSoundGroup(blockState).getHitSound(), 1, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            }
        }
    }

    @Override
    protected void age() {}

    private boolean seeking() {
        return this.ticksSeeking > 0;
    }

    private boolean isClient() {
        return this.world.isClient;
    }

    private boolean isServer() {
        return !this.isClient();
    }
}
