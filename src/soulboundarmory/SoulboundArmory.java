package soulboundarmory;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import soulboundarmory.enchantment.ImpactEnchantment;

@Mod(SoulboundArmory.ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class SoulboundArmory {
    public static final String ID = "soulbound-armory";

    public static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(id("main"), () -> "0", "0"::equals, "0"::equals);

    public static final Enchantment impact = new ImpactEnchantment();

    public static ResourceLocation id(String path) {
        return new ResourceLocation(ID, path);
    }

    public SoulboundArmory() {
        // ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, );
        // ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, (minecaft, parent) -> );
    }
}
