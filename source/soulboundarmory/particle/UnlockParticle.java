package soulboundarmory.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.particle.TotemParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import org.jetbrains.annotations.Nullable;

public class UnlockParticle extends TotemParticle {
	public UnlockParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider sprite) {
		super(world, x, y, z, velocityX, velocityY, velocityZ, sprite);

		var r = this.random.nextFloat();
		var g = this.random.nextFloat();
		var b = this.random.nextFloat();

		if (this.random.nextInt(4) == 0) {
			this.setColor(0.7F + r * 0.3F, 0.5F + g * 0.3F, b * 0.2F);
		} else {
			this.setColor(0.8F + r * 0.2F, 0.4F + g * 0.3F, b * 0.2F);
		}
	}

	public static final class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider sprite;

		public Factory(SpriteProvider sprite) {
			this.sprite = sprite;
		}

		@Nullable
		@Override
		public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
			return new UnlockParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.sprite);
		}
	}
}
