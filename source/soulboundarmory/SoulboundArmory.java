package soulboundarmory;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import soulboundarmory.enchantment.ImpactEnchantment;

@Mod(SoulboundArmory.ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public final class SoulboundArmory {
    public static final String ID = "soulboundarmory";

    public static final String componentKey = id("components").toString();
    public static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(id("main"), () -> "0", "0"::equals, "0"::equals);
    public static final Enchantment impact = new ImpactEnchantment();
    public static final DefaultParticleType criticalHitParticleType = new DefaultParticleType(false);

    public static Identifier id(String path) {
        return new Identifier(ID, path);
    }

    public SoulboundArmory() {
        // ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, );
        // ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, (minecaft, parent) -> );
    }
}
