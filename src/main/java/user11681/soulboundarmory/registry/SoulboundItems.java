package user11681.soulboundarmory.registry;

import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.item.SoulboundDagger;
import user11681.soulboundarmory.item.SoulboundGreatswordItem;
import user11681.soulboundarmory.item.SoulboundPickItem;
import user11681.soulboundarmory.item.SoulboundStaffItem;
import user11681.soulboundarmory.item.SoulboundSwordItem;

public class SoulboundItems {
    public static final SoulboundDagger dagger = new SoulboundDagger();
    public static final SoulboundSwordItem sword = new SoulboundSwordItem();
    public static final SoulboundGreatswordItem greatsword = new SoulboundGreatswordItem();
    public static final SoulboundStaffItem staff = new SoulboundStaffItem();
    public static final SoulboundPickItem pick = new SoulboundPickItem();

    private static void register(String name, Item item) {
        Registry.register(Registry.ITEM, SoulboundArmory.id("soulbound_" + name), item);
    }

    public static void register() {
        register("dagger", dagger);
        register("sword", sword);
        register("greatsword", greatsword);
        register("staff", staff);
        register("pick", pick);
    }
}
