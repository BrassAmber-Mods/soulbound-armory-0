package user11681.soulboundarmory.component.statistics;

import com.mojang.serialization.Lifecycle;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.util.Util;

public class Category {
    public static final Registry<Category> registry = new SimpleRegistry<>(RegistryKey.ofRegistry(SoulboundArmory.id("category")), Lifecycle.stable());

    public static final Category datum = register("datum");
    public static final Category attribute = register("attribute");
    public static final Category enchantment = register("enchantment");
    public static final Category skill = register("skill");
    public static final Registry<Category> category = Util.simpleRegistry("category");

    public Identifier identifier() {
        return registry.getId(this);
    }

    private static Category register(String path) {
        return Registry.register(registry, SoulboundArmory.id(path), new Category());
    }
}
