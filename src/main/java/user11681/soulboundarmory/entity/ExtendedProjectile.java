package user11681.soulboundarmory.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.world.World;
import user11681.soulboundarmory.asm.access.entity.PersistentProjectileEntityAccess;
import user11681.usersmanual.math.MathUtil;

@SuppressWarnings("EntityConstructor")
public abstract class ExtendedProjectile extends PersistentProjectileEntity {
    protected ExtendedProjectile(final EntityType<? extends PersistentProjectileEntity> type, final double x, final double y, final double z, final World world) {
        super(type, x, y, z, world);
    }

    protected ExtendedProjectile(final EntityType<? extends PersistentProjectileEntity> type, final LivingEntity owner, final World world) {
        super(type, owner, world);
    }

    public ExtendedProjectile(final EntityType<? extends PersistentProjectileEntity> type, final World world) {
        super(type, world);
    }

    public ExtendedProjectile(final World world, final EntityType<? extends PersistentProjectileEntity> type, final double x, final double y, final double z) {
        super(type, x, y, z, world);
    }

    public double displacementTo(final Entity entity) {
        return this.displacementTo(entity.getX(), entity.getY(), entity.getZ());
    }

    public double displacementTo(final double x, final double y, final double z) {
        return this.distanceTo(x, y, z) * MathUtil.signum(x - this.getX(), y - this.getY(), z - this.getZ());
    }

    public double distanceTo(final double x, final double y, final double z) {
        return Math.sqrt(this.squaredDistanceTo(x, y, z));
    }

    public double getSpeed() {
        return Math.sqrt(this.velocityX() * this.velocityX()
                + this.velocityY() * this.velocityY()
                + this.velocityZ() * this.velocityZ()
        );
    }

    public double velocityX() {
        return this.getVelocity().x;
    }

    public double velocityY() {
        return this.getVelocity().y;
    }

    public double velocityZ() {
        return this.getVelocity().z;
    }

    public double getVelocityD() {
        return this.getSpeed() * MathUtil.signum(this.velocityX(), this.velocityY(), this.velocityZ());
    }

    protected int getLife() {
        return ((PersistentProjectileEntityAccess) this).getLife();
    }
}
