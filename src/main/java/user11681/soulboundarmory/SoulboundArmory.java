package user11681.soulboundarmory;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import user11681.soulboundarmory.enchantment.ImpactEnchantment;

@Mod(SoulboundArmory.ID)
@ObjectHolder(SoulboundArmory.ID)
public class SoulboundArmory {
    public static final String ID = "soulbound_armory";
    public static final String NAME = "soulbound armory";

    public static final Logger logger = LogManager.getLogger(ID);

    @ObjectHolder("impact")
    public static final Enchantment impact = new ImpactEnchantment();

    public static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(id("main"), () -> "1", "1"::equals, "1"::equals);

    public static ResourceLocation id(String path) {
        return new ResourceLocation(ID, path);
    }
}
