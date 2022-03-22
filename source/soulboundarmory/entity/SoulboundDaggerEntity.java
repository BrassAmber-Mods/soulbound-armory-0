package soulboundarmory.entity;

import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.item.weapon.DaggerComponent;
import soulboundarmory.skill.Skills;
import soulboundarmory.lib.transform.Register;
import soulboundarmory.util.Util;

public class SoulboundDaggerEntity extends ExtendedProjectile implements IEntityAdditionalSpawnData {
    @Register("dagger") public static final EntityType<SoulboundDaggerEntity> type = EntityType.Builder
        .create((EntityType.EntityFactory<SoulboundDaggerEntity>) SoulboundDaggerEntity::new, SpawnGroup.MISC)
        .setDimensions(.3F, .5F)
        .build(Util.id("dagger").toString());

    public static SoulboundDaggerEntity attacker;

    public double damageRatio;

    protected final boolean clone;
    protected final boolean spawnClone;
    protected boolean hit;
    protected int ticksSeeking;
    protected int ticksInGround;
    protected ItemStack stack = ItemStack.EMPTY;

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
        return this.component().map(ItemComponent::stack).orElse(this.stack);
    }

    @Override
    public byte getPierceLevel() {
        return -1;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.inGround) {
            this.ticksInGround++;
        } else {
            this.ticksInGround = 0;
        }

        top: if (this.component().isPresent()) {
            var component = this.component().get();
            var owner = component.player;
            var attackSpeed = component.attackSpeed();

            if (this.seeking()
                || component.hasSkill(Skills.sneakReturn) && owner.isSneaking() && this.age >= 60 / attackSpeed
                || component.hasSkill(Skills.returning) && (this.ticksInGround >= 60 / attackSpeed || this.getY() < this.world.getBottomY())
            ) {
                this.inGround = false;
                this.setNoClip(true);
                this.setYaw((float) Math.toDegrees(MathHelper.atan2(this.velocityX(), this.velocityZ())));
                var velocity = this.getOwner().getEyePos().subtract(this.getPos()).normalize();

                if (!this.seeking()) {
                    this.setVelocity(velocity);
                } else {
                    this.setVelocity(velocity.multiply(Math.min(3, this.getVelocity().length() * 1.03)));
                }

                this.ticksSeeking++;
            }
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (this.isServer() && player == this.getOwner()) {
            if (this.clone || (this.hit || this.age >= Math.max(1, 20 / this.component().get().attackSpeed())) && player.getInventory().insertStack(this.asItemStack())) {
                player.sendPickup(this, 1);
                this.discard();
            }
        }
    }

    @Override
    public boolean isClient() {
        return this.world.isClient;
    }

    @Override public void writeSpawnData(PacketByteBuf buffer) {
        buffer.writeItemStack(this.asItemStack());
    }

    @Override public void readSpawnData(PacketByteBuf buffer) {
        this.stack = buffer.readItemStack();
    }

    @Override public Packet<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void onEntityHit(EntityHitResult target) {
        this.hit = true;

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
            this.setSound(null);
            super.onBlockHit(result);

            if (this.isServer()) {
                var blockState = this.world.getBlockState(result.getBlockPos());
                this.playSound(blockState.getBlock().getSoundGroup(blockState).getBreakSound(), 1, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            }
        }

        if (this.clone && this.component().filter(component -> component.hasSkill(Skills.returning)).isEmpty()) {
            this.discard();
        }
    }

    @Override
    protected void age() {}

    private boolean seeking() {
        return this.ticksSeeking > 0;
    }
}
