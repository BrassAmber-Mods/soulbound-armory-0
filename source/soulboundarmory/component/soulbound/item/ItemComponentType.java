package soulboundarmory.component.soulbound.item;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.tool.PickComponent;
import soulboundarmory.component.soulbound.item.tool.ToolComponent;
import soulboundarmory.component.soulbound.item.weapon.BigswordComponent;
import soulboundarmory.component.soulbound.item.weapon.DaggerComponent;
import soulboundarmory.component.soulbound.item.weapon.GreatswordComponent;
import soulboundarmory.component.soulbound.item.weapon.SwordComponent;
import soulboundarmory.component.soulbound.item.weapon.TridentComponent;
import soulboundarmory.component.soulbound.item.weapon.WeaponComponent;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.lib.component.EntityComponentKey;
import soulboundarmory.registry.RegistryElement;
import soulboundarmory.util.Util;

public final class ItemComponentType<T extends ItemComponent<T>> extends RegistryElement<ItemComponentType<T>> {
    public static final Registry registry = Util.newRegistry("storage");

    public static final ItemComponentType<DaggerComponent> dagger = weapon("dagger");
    public static final ItemComponentType<SwordComponent> sword = weapon("sword");
    public static final ItemComponentType<GreatswordComponent> greatsword = weapon("greatsword");
    public static final ItemComponentType<BigswordComponent> bigsword = weapon("bigsword");
    public static final ItemComponentType<TridentComponent> trident = weapon("trident");
    public static final ItemComponentType<PickComponent> pick = tool("pick");

    public final EntityComponentKey<? extends SoulboundComponent<?>> parentKey;

    public ItemComponentType(String path, EntityComponentKey<? extends SoulboundComponent<?>> key) {
        super(path);

        this.parentKey = key;
    }

    public static <T extends WeaponComponent<T>> ItemComponentType<T> weapon(String path) {
        return new ItemComponentType<>(path, Components.weapon);
    }

    public static <T extends ToolComponent<T>> ItemComponentType<T> tool(String path) {
        return new ItemComponentType<>(path, Components.tool);
    }

    public static ItemComponentType<?> get(Identifier id) {
        return Util.cast(registry.get(id));
    }

    public static ItemComponentType<?> get(String name) {
        return get(new Identifier(name));
    }

    public T of(Entity entity) {
        return this.parentKey.nullable(entity).map(component -> component.item(this)).orElse(null);
    }

    public Optional<T> nullable(Entity entity) {
        return Optional.ofNullable(this.of(entity));
    }

    @Override
    public String toString() {
        return "item component type " + this.id;
    }
}
