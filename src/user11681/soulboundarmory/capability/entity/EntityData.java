package user11681.soulboundarmory.capability.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import user11681.soulboundarmory.capability.EntityCapability;
import user11681.soulboundarmory.serial.CompoundSerializable;
import user11681.soulboundarmory.util.Util;

public class EntityData extends EntityCapability<Entity> implements CompoundSerializable {
    protected int freezeTicks;
    protected int blockTeleportTicks;

    public EntityData(Entity entity) {
        super(entity);
    }

    public boolean cannotTeleport() {
        return this.blockTeleportTicks > 0;
    }

    public void blockTeleport(int ticks) {
        this.blockTeleportTicks = ticks;
    }

    public void freeze(PlayerEntity freezer, int ticks, float damage) {
        if (!freezer.world.isClient) {
            ((ServerWorld) freezer.world).spawnParticles(
                    ParticleTypes.ITEM_SNOWBALL,
                    this.entity.getX(), this.entity.getEyeY(), this.entity.getZ(), 32, 0.1, 0, 0.1, 2D);
            this.entity.playSound(SoundEvents.BLOCK_SNOW_HIT, 1, 1.2F / (this.entity.world.random.nextFloat() * 0.2F + 0.9F));
            this.entity.extinguish();

            if (ticks > this.freezeTicks) {
                this.freezeTicks = ticks;
            }

            if (this.entity instanceof LivingEntity) {
                this.entity.damage(DamageSource.player(freezer), damage);
            }

            if (this.entity instanceof CreeperEntity) {
                ((CreeperEntity) this.entity).setFuseSpeed(-1);
            }

            if (this.entity instanceof ProjectileEntity) {
                this.entity.setVelocity(0, this.entity.getVelocity().y, 0);
            }
        }
    }

    public boolean canBeFrozen() {
        return (!(this.entity instanceof PlayerEntity) || Util.server().isPvpEnabled()) && this.entity.isAlive();
    }

    public boolean isFrozen() {
        return this.freezeTicks > 0 && this.entity.isAlive() && !this.entity.isOnFire();
    }

    public void tick() {
        if (!this.entity.world.isClient) {
            if (this.isFrozen() && this.entity instanceof LivingEntity entity) {
                if (entity.hurtTime > 0) {
                    entity.hurtTime--;
                }

                this.freezeTicks--;
            } else {
                this.freezeTicks = 0;
            }

            if (this.cannotTeleport()) {
                this.blockTeleportTicks--;
            }
        }
    }

    @Override
    public void serializeNBT(NbtCompound tag) {
        this.freezeTicks = tag.getInt("freezeTicks");
        this.blockTeleportTicks = tag.getInt("blockTeleportTicks");
    }

    @Override
    public void deserializeNBT(NbtCompound tag) {
        tag.putInt("freezeTicks", this.freezeTicks);
        tag.putInt("blockTeleportTicks", this.blockTeleportTicks);
    }
}
