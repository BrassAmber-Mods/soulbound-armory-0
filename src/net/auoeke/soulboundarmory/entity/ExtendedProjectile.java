package net.auoeke.soulboundarmory.entity;

import net.auoeke.soulboundarmory.asm.access.entity.AbstractArrowEntityAccess;
import net.auoeke.soulboundarmory.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.world.World;

public abstract class ExtendedProjectile extends AbstractArrowEntity {
    protected ExtendedProjectile(EntityType<? extends AbstractArrowEntity> type, double x, double y, double z, World world) {
        super(type, x, y, z, world);
    }

    protected ExtendedProjectile(EntityType<? extends AbstractArrowEntity> type, LivingEntity owner, World world) {
        super(type, owner, world);
    }

    public ExtendedProjectile(EntityType<? extends AbstractArrowEntity> type, World world) {
        super(type, world);
    }

    public ExtendedProjectile(World world, EntityType<? extends AbstractArrowEntity> type, double x, double y, double z) {
        super(type, x, y, z, world);
    }

    public double displacement(Entity entity) {
        return this.displacement(entity.getX(), entity.getY(), entity.getZ());
    }

    public double displacement(double x, double y, double z) {
        return this.distance(x, y, z) * MathUtil.signum(x - this.getX(), y - this.getY(), z - this.getZ());
    }

    public double distance(double x, double y, double z) {
        return Math.sqrt(this.distanceToSqr(x, y, z));
    }

    public double getSpeed() {
        return Math.sqrt(
            this.velocityX() * this.velocityX()
                + this.velocityY() * this.velocityY()
                + this.velocityZ() * this.velocityZ()
        );
    }

    public double velocityX() {
        return this.getDeltaMovement().x;
    }

    public double velocityY() {
        return this.getDeltaMovement().y;
    }

    public double velocityZ() {
        return this.getDeltaMovement().z;
    }

    public double getVelocityD() {
        return this.getSpeed() * MathUtil.signum(this.velocityX(), this.velocityY(), this.velocityZ());
    }

    protected int life() {
        return ((AbstractArrowEntityAccess) this).life();
    }
}
