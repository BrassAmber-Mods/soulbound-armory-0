package soulboundarmory.component.statistics;

import net.minecraft.util.registry.Registry;
import soulboundarmory.registry.RegistryElement;
import soulboundarmory.util.Util;

public class Category extends RegistryElement<Category> {
    public static final Registry<Category> registry = Util.newRegistry("category");

    public static final Category datum = new Category("datum");
    public static final Category attribute = new Category("attribute");
    public static final Category enchantment = new Category("enchantment");
    public static final Category skill = new Category("skill");

    public Category(String path) {
        super(path);
    }

    @Override
    public String toString() {
        return "category " + this.id();
    }
}
