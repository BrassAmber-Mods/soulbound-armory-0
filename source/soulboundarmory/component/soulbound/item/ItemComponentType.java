package soulboundarmory.component.soulbound.item;

import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.IForgeRegistry;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.tool.PickComponent;
import soulboundarmory.component.soulbound.item.weapon.DaggerComponent;
import soulboundarmory.component.soulbound.item.weapon.GreatswordComponent;
import soulboundarmory.component.soulbound.item.weapon.StaffComponent;
import soulboundarmory.component.soulbound.item.weapon.SwordComponent;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.registry.RegistryEntry;
import soulboundarmory.util.Util;

@SuppressWarnings("unchecked")
public final class ItemComponentType<T extends ItemComponent<T>> extends RegistryEntry<ItemComponentType<T>> {
    @SuppressWarnings("rawtypes")
    public static final IForgeRegistry registry = Util.<ItemComponentType>registry("storage");

    public static final ItemComponentType<DaggerComponent> dagger = register("dagger");
    public static final ItemComponentType<SwordComponent> sword = register("sword");
    public static final ItemComponentType<GreatswordComponent> greatsword = register("greatsword");
    public static final ItemComponentType<StaffComponent> staff = register("staff");
    public static final ItemComponentType<PickComponent> pick = register("pick");

    @SuppressWarnings("unchecked")
    public static <T extends ItemComponent<T>> IForgeRegistry<ItemComponentType<T>> registry() {
        return registry;
    }

    public static ItemComponentType<?> get(Identifier id) {
        return registry().getValue(id);
    }

    public static ItemComponentType<?> get(String name) {
        return get(new Identifier(name));
    }

    public T get(Entity entity) {
        return Components.soulbound(entity).map(this::get).filter(Objects::nonNull).findAny().orElse(null);
    }

    public T get(SoulboundComponent component) {
        return component.item(this);
    }

    @Override
    public String toString() {
        return "item component type " + this.getRegistryName();
    }
}
