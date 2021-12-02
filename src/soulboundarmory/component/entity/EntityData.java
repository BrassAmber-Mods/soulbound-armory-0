package soulboundarmory.component.entity;

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
import soulboundarmory.component.EntityComponent;

public class EntityData extends EntityComponent<Entity> {
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
        if (!freezer.world.isRemote) {
            ((ServerWorld) freezer.world).spawnParticle(
                ParticleTypes.ITEM_SNOWBALL,
                this.entity.getPosX(), this.entity.getEyeHeight(), this.entity.getPosZ(), 32, 0.1, 0, 0.1, 2D
            );

            this.entity.playSound(SoundEvents.BLOCK_SNOW_HIT, 1, 1.2F / (this.entity.world.rand.nextFloat() * 0.2F + 0.9F));
            this.entity.extinguish();

            if (ticks > this.freezeTicks) {
                this.freezeTicks = ticks;
            }

            if (this.entity instanceof LivingEntity) {
                this.entity.attackEntityFrom(DamageSource.causePlayerDamage(freezer), damage);
            }

            if (this.entity instanceof CreeperEntity) {
                ((CreeperEntity) this.entity).setCreeperState(-1);
            }

            if (this.entity instanceof ProjectileEntity) {
                this.entity.setMotion(0, this.entity.getMotion().y, 0);
            }
        }
    }

    public boolean canBeFrozen() {
        return (!(this.entity instanceof PlayerEntity) || this.entity.getServer().isPVPEnabled()) && this.entity.isAlive();
    }

    public boolean isFrozen() {
        return this.freezeTicks > 0 && this.entity.isAlive() && !this.entity.isBurning();
    }

    public void tick() {
        if (!this.entity.world.isRemote) {
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
    public void serialize(CompoundNBT tag) {
        tag.putInt("freezeTicks", this.freezeTicks);
        tag.putInt("blockTeleportTicks", this.blockTeleportTicks);
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        this.freezeTicks = tag.getInt("freezeTicks");
        this.blockTeleportTicks = tag.getInt("blockTeleportTicks");
    }
}
