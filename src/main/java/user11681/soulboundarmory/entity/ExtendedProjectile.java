package user11681.soulboundarmory.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.world.World;
import user11681.soulboundarmory.asm.access.entity.AbstractArrowEntityAccess;
import user11681.soulboundarmory.util.MathUtil;

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

    public double displacementTo(Entity entity) {
        return this.displacementTo(entity.getX(), entity.getY(), entity.getZ());
    }

    public double displacementTo(double x, double y, double z) {
        return this.distanceTo(x, y, z) * MathUtil.signum(x - this.getX(), y - this.getY(), z - this.getZ());
    }

    public double distanceTo(double x, double y, double z) {
        return Math.sqrt(this.distanceToSqr(x, y, z));
    }

    public double getSpeed() {
        return Math.sqrt(this.velocityX() * this.velocityX()
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

    public double getDeltaMovementD() {
        return this.getSpeed() * MathUtil.signum(this.velocityX(), this.velocityY(), this.velocityZ());
    }

    protected int getLife() {
        return ((AbstractArrowEntityAccess) this).getLife();
    }
}
