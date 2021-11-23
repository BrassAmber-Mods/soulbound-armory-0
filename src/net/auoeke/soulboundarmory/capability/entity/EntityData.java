package net.auoeke.soulboundarmory.capability.entity;

import net.auoeke.soulboundarmory.capability.EntityCapability;
import net.auoeke.soulboundarmory.serial.CompoundSerializable;
import net.auoeke.soulboundarmory.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.server.ServerWorld;

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
        if (!freezer.level.isClientSide) {
            ((ServerWorld) freezer.level).sendParticles(
                    ParticleTypes.ITEM_SNOWBALL,
                    this.entity.getX(), this.entity.getEyeY(), this.entity.getZ(), 32, 0.1, 0, 0.1, 2D);
            this.entity.playSound(SoundEvents.SNOW_HIT, 1, 1.2F / (this.entity.level.random.nextFloat() * 0.2F + 0.9F));
            this.entity.clearFire();

            if (ticks > this.freezeTicks) {
                this.freezeTicks = ticks;
            }

            if (this.entity instanceof LivingEntity) {
                this.entity.hurt(DamageSource.playerAttack(freezer), damage);
            }

            if (this.entity instanceof CreeperEntity) {
                ((CreeperEntity) this.entity).setSwellDir(-1);
            }

            if (this.entity instanceof ProjectileEntity) {
                this.entity.setDeltaMovement(0, this.entity.getDeltaMovement().y, 0);
            }
        }
    }

    public boolean canBeFrozen() {
        return (!(this.entity instanceof PlayerEntity) || Util.server().isPvpAllowed()) && this.entity.isAlive();
    }

    public boolean isFrozen() {
        return this.freezeTicks > 0 && this.entity.isAlive() && !this.entity.isOnFire();
    }

    public void tick() {
        if (!this.entity.level.isClientSide) {
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
    public void serializeNBT(CompoundNBT tag) {
        this.freezeTicks = tag.getInt("freezeTicks");
        this.blockTeleportTicks = tag.getInt("blockTeleportTicks");
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        tag.putInt("freezeTicks", this.freezeTicks);
        tag.putInt("blockTeleportTicks", this.blockTeleportTicks);
    }
}
