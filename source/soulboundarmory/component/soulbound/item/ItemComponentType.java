package soulboundarmory.component.soulbound.item;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.IForgeRegistry;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.tool.PickComponent;
import soulboundarmory.component.soulbound.item.tool.ToolComponent;
import soulboundarmory.component.soulbound.item.weapon.BigswordComponent;
import soulboundarmory.component.soulbound.item.weapon.DaggerComponent;
import soulboundarmory.component.soulbound.item.weapon.GreatswordComponent;
import soulboundarmory.component.soulbound.item.weapon.SwordComponent;
import soulboundarmory.component.soulbound.item.weapon.TridentComponent;
import soulboundarmory.component.soulbound.item.weapon.WeaponComponent;
import soulboundarmory.component.soulbound.player.MasterComponent;
import soulboundarmory.lib.component.EntityComponentKey;
import soulboundarmory.lib.transform.Register;
import soulboundarmory.registry.RegistryElement;

public final class ItemComponentType<C extends ItemComponent<C>> extends RegistryElement<ItemComponentType<C>> {
    @Register("dagger") public static final ItemComponentType<DaggerComponent> dagger = weapon();
    @Register("sword") public static final ItemComponentType<SwordComponent> sword = weapon();
    @Register("bigsword") public static final ItemComponentType<BigswordComponent> bigsword = weapon();
    @Register("greatsword") public static final ItemComponentType<GreatswordComponent> greatsword = weapon();
    @Register("trident") public static final ItemComponentType<TridentComponent> trident = weapon();
    @Register("pick") public static final ItemComponentType<PickComponent> pick = tool();

    public final EntityComponentKey<? extends MasterComponent<?>> parentKey;

    public ItemComponentType(EntityComponentKey<? extends MasterComponent<?>> key) {
        this.parentKey = key;
    }

    @Register("item_component") public static native <C extends ItemComponent<C>> IForgeRegistry<ItemComponentType<C>> registry();

    public static <T extends WeaponComponent<T>> ItemComponentType<T> weapon() {
        return new ItemComponentType<>(Components.weapon);
    }

    public static <T extends ToolComponent<T>> ItemComponentType<T> tool() {
        return new ItemComponentType<>(Components.tool);
    }

    public static ItemComponentType<?> get(Identifier id) {
        return registry().getValue(id);
    }

    public static ItemComponentType<?> get(String name) {
        return get(new Identifier(name));
    }

    public C of(Entity entity) {
        return this.parentKey.nullable(entity).map(component -> component.item(this)).orElse(null);
    }

    public Optional<C> nullable(Entity entity) {
        return Optional.ofNullable(this.of(entity));
    }

    @Override
    public String toString() {
        return "item component type " + this.id();
    }
}
