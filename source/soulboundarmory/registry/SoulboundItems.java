package soulboundarmory.registry;

import net.minecraft.item.Item;
import soulboundarmory.item.SoulboundDagger;
import soulboundarmory.item.SoulboundGreatsword;
import soulboundarmory.item.SoulboundPickItem;
import soulboundarmory.item.SoulboundStaffItem;
import soulboundarmory.item.SoulboundSword;

public class SoulboundItems {
    public static final SoulboundDagger dagger = name(new SoulboundDagger(), "dagger");
    public static final SoulboundSword sword = name(new SoulboundSword(), "sword");
    public static final SoulboundGreatsword greatsword = name(new SoulboundGreatsword(), "greatsword");
    public static final SoulboundStaffItem staff = name(new SoulboundStaffItem(), "staff");
    public static final SoulboundPickItem pick = name(new SoulboundPickItem(), "pick");

    private static <T extends Item> T name(T item, String path) {
        return (T) item.setRegistryName(path);
    }
}
