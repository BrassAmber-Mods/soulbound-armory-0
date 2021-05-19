package user11681.soulboundarmory.capability.statistics;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.util.Util;

public class Category extends ForgeRegistryEntry<Category> {
    public static final IForgeRegistry<Category> registry = Util.registry("category");

    public static final Category datum = register("datum");
    public static final Category attribute = register("attribute");
    public static final Category enchantment = register("enchantment");
    public static final Category skill = register("skill");

    public Category(ResourceLocation identifier) {
        this.setRegistryName(identifier);
    }

    public ResourceLocation identifier() {
        return registry.getKey(this);
    }

    private static Category register(String path) {
        Category category = new Category(SoulboundArmory.id(path));
        registry.register(category);

        return category;
    }
}
