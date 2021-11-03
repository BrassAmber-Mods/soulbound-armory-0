package user11681.soulboundarmory.capability.statistics;

import net.minecraftforge.registries.IForgeRegistry;
import user11681.soulboundarmory.registry.RegistryEntry;
import user11681.soulboundarmory.util.Util;

public class Category extends RegistryEntry<Category> {
    public static final IForgeRegistry<Category> registry = Util.registry("category");

    public static final Category datum = register("datum");
    public static final Category attribute = register("attribute");
    public static final Category enchantment = register("enchantment");
    public static final Category skill = register("skill");
}
