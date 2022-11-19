package soulboundarmory.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import soulboundarmory.util.Math2;
import soulboundarmory.util.Sided;

public abstract class ExtendedProjectile extends PersistentProjectileEntity implements Sided {
	protected ExtendedProjectile(EntityType<? extends PersistentProjectileEntity> type, double x, double y, double z, World world) {
		super(type, x, y, z, world);
	}

	protected ExtendedProjectile(EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world) {
		super(type, owner, world);
	}

	public ExtendedProjectile(EntityType<? extends PersistentProjectileEntity> type, World world) {
		super(type, world);
	}

	public ExtendedProjectile(World world, EntityType<? extends PersistentProjectileEntity> type, double x, double y, double z) {
		super(type, x, y, z, world);
	}

	public double displacement(Entity entity) {
		return this.displacement(entity.getX(), entity.getY(), entity.getZ());
	}

	public double displacement(double x, double y, double z) {
		return this.distance(x, y, z) * Math2.signum(x - this.getX(), y - this.getY(), z - this.getZ());
	}

	public double distance(double x, double y, double z) {
		return Math.sqrt(this.squaredDistanceTo(x, y, z));
	}

	public double speed() {
		return MathHelper.magnitude(this.velocityX(), this.velocityY(), this.velocityZ());
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

	public double velocityD() {
		return this.speed() * Math2.signum(this.velocityX(), this.velocityY(), this.velocityZ());
	}
}
