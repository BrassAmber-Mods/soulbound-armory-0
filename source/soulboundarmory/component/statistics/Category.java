package soulboundarmory.component.statistics;

import net.minecraftforge.registries.IForgeRegistry;
import soulboundarmory.registry.RegistryEntry;
import soulboundarmory.util.Util;

public class Category extends RegistryEntry<Category> {
    public static final IForgeRegistry<Category> registry = Util.newRegistry("category");

    public static final Category datum = new Category("datum");
    public static final Category attribute = new Category("attribute");
    public static final Category enchantment = new Category("enchantment");
    public static final Category skill = new Category("skill");

    public Category(String path) {
        super(path);
    }

    @Override
    public String toString() {
        return "category " + this.getRegistryName();
    }
}
