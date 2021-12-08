package soulboundarmory.component.soulbound.item;

import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.IForgeRegistry;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.tool.PickStorage;
import soulboundarmory.component.soulbound.item.weapon.DaggerStorage;
import soulboundarmory.component.soulbound.item.weapon.GreatswordStorage;
import soulboundarmory.component.soulbound.item.weapon.StaffStorage;
import soulboundarmory.component.soulbound.item.weapon.SwordStorage;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.registry.RegistryEntry;
import soulboundarmory.util.Util;

@SuppressWarnings("unchecked")
public final class StorageType<T extends ItemStorage<T>> extends RegistryEntry<StorageType<T>> {
    @SuppressWarnings("rawtypes")
    public static final IForgeRegistry registry = Util.<StorageType>registry("storage");

    public static final StorageType<DaggerStorage> dagger = register("dagger");
    public static final StorageType<SwordStorage> sword = register("sword");
    public static final StorageType<GreatswordStorage> greatsword = register("greatsword");
    public static final StorageType<StaffStorage> staff = register("staff");
    public static final StorageType<PickStorage> pick = register("pick");

    @SuppressWarnings("unchecked")
    public static <T extends ItemStorage<T>> IForgeRegistry<StorageType<T>> registry() {
        return registry;
    }

    public static StorageType<?> get(Identifier id) {
        return registry().getValue(id);
    }

    public static StorageType<?> get(String name) {
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
        return "storage type " + this.getRegistryName();
    }
}
