package soulboundarmory;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.sound.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import soulboundarmory.lib.transform.Register;
import soulboundarmory.util.Util;

@Mod(SoulboundArmory.ID)
public final class SoulboundArmory {
    public static final String ID = "soulboundarmory";

    public static final String componentKey = Util.id("components").toString();
    public static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(Util.id("main"), () -> "0", "0"::equals, "0"::equals);
    @Register("critical_hit") public static final DefaultParticleType criticalHitParticleType = new DefaultParticleType(false);
    @Register("unlock") public static final DefaultParticleType unlockParticle = new DefaultParticleType(false);
    @Register("unlock") public static final SoundEvent unlockSound = new SoundEvent(Util.id("unlock"));

    public SoulboundArmory() {
        // ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, );
        // ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, (minecaft, parent) -> );
    }
}
