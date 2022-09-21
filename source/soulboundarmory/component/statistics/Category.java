package soulboundarmory.component.statistics;

import net.minecraftforge.registries.IForgeRegistry;
import soulboundarmory.module.transform.Register;
import soulboundarmory.module.transform.RegisterAll;
import soulboundarmory.module.transform.Registry;
import soulboundarmory.registry.Identifiable;

@RegisterAll(type = Category.class, registry = "category")
public class Category extends Identifiable {
    @Register("datum") public static final Category datum = new Category();
    @Register("attribute") public static final Category attribute = new Category();
    @Register("enchantment") public static final Category enchantment = new Category();
    @Register("skill") public static final Category skill = new Category();

    @Registry("category") public static native IForgeRegistry<Category> registry();

    @Override
    public String toString() {
        return "category " + this.id();
    }
}
