package soulboundarmory.registry;

import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;
import soulboundarmory.item.SoulboundDagger;
import soulboundarmory.item.SoulboundGreatsword;
import soulboundarmory.item.SoulboundPickItem;
import soulboundarmory.item.SoulboundStaffItem;
import soulboundarmory.item.SoulboundSword;
import soulboundarmory.util.Util;

public class SoulboundItems {
    public static final ToolMaterial material = TierSortingRegistry.registerTier(new ForgeTier(3, 0, 1.5F, 0, 0, null, () -> null), Util.id("material"), List.of(), List.of());

    public static final SoulboundDagger dagger = name(new SoulboundDagger(), "dagger");
    public static final SoulboundSword sword = name(new SoulboundSword(), "sword");
    public static final SoulboundGreatsword greatsword = name(new SoulboundGreatsword(), "greatsword");
    public static final SoulboundStaffItem staff = name(new SoulboundStaffItem(), "staff");
    public static final SoulboundPickItem pick = name(new SoulboundPickItem(), "pick");

    private static <T extends Item> T name(T item, String path) {
        return (T) item.setRegistryName(path);
    }
}
