package soulboundarmory;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soulboundarmory.module.transform.Register;

@Mod(SoulboundArmory.ID)
public final class SoulboundArmory {
	public static final String ID = "soulboundarmory";

	public static final Logger logger = LogManager.getLogger(ID);
	public static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(id("main"), () -> "0", "0"::equals, "0"::equals);
	@Register(value = "critical_hit", registry = "particle_type") public static final DefaultParticleType criticalHitParticleType = new DefaultParticleType(false);
	@Register(value = "unlock", registry = "particle_type") public static final DefaultParticleType unlockParticle = new DefaultParticleType(false);
	@Register(value = "unlock", registry = "sound_event") public static final SoundEvent unlockSound = new SoundEvent(id("unlock"));

	public static Identifier id(String path) {
		return new Identifier(ID, path);
	}
}
