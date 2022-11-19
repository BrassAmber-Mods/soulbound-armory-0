package soulboundarmory.particle;

import net.minecraft.client.particle.DamageParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import org.jetbrains.annotations.Nullable;
import soulboundarmory.util.Math2;

public class CriticalHitParticle extends DamageParticle {
	public CriticalHitParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider sprite) {
		super(world, x, y, z, velocityX, velocityY, velocityZ);

		this.setSprite(sprite);

		var color = 0x6B0303;
		this.red = Math2.redf(color);
		this.green = Math2.greenf(color);
		this.blue = Math2.bluef(color);
	}

	public static final class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider sprite;

		public Factory(SpriteProvider sprite) {
			this.sprite = sprite;
		}

		@Nullable
		@Override public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
			return new CriticalHitParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.sprite);
		}
	}
}
