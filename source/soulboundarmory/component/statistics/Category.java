package soulboundarmory.component.statistics;

import net.minecraftforge.registries.IForgeRegistry;
import soulboundarmory.module.transform.Register;
import soulboundarmory.registry.RegistryElement;

public class Category extends RegistryElement<Category> {
    @Register("datum") public static final Category datum = new Category();
    @Register("attribute") public static final Category attribute = new Category();
    @Register("enchantment") public static final Category enchantment = new Category();
    @Register("skill") public static final Category skill = new Category();

    @Register("category") public static native IForgeRegistry<Category> registry();

    @Override
    public String toString() {
        return "category " + this.id();
    }
}
