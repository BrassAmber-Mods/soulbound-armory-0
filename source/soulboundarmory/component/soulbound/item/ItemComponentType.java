package soulboundarmory.component.soulbound.item;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.IForgeRegistry;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.tool.PickComponent;
import soulboundarmory.component.soulbound.item.weapon.BigswordComponent;
import soulboundarmory.component.soulbound.item.weapon.DaggerComponent;
import soulboundarmory.component.soulbound.item.weapon.GreatswordComponent;
import soulboundarmory.component.soulbound.item.weapon.StaffComponent;
import soulboundarmory.component.soulbound.item.weapon.SwordComponent;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.lib.component.EntityComponentKey;
import soulboundarmory.registry.RegistryEntry;
import soulboundarmory.util.Util;

@SuppressWarnings("unchecked")
public final class ItemComponentType<T extends ItemComponent<T>> extends RegistryEntry<ItemComponentType<T>> {
    @SuppressWarnings("rawtypes")
    public static final IForgeRegistry registry = Util.<ItemComponentType>newRegistry("storage");

    public static final ItemComponentType<DaggerComponent> dagger = new ItemComponentType<>("dagger", Components.weapon);
    public static final ItemComponentType<SwordComponent> sword = new ItemComponentType<>("sword", Components.weapon);
    public static final ItemComponentType<GreatswordComponent> greatsword = new ItemComponentType<>("greatsword", Components.weapon);
    public static final ItemComponentType<BigswordComponent> bigsword = new ItemComponentType<>("bigsword", Components.weapon);
    public static final ItemComponentType<StaffComponent> staff = new ItemComponentType<>("staff", Components.weapon);
    public static final ItemComponentType<PickComponent> pick = new ItemComponentType<>("pick", Components.tool);

    public final EntityComponentKey<? extends SoulboundComponent<?>> parentKey;

    public ItemComponentType(String path, EntityComponentKey<? extends SoulboundComponent<?>> key) {
        super(path);

        this.parentKey = key;
    }

    public static ItemComponentType<?> get(Identifier id) {
        return Util.cast(registry.getValue(id));
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
