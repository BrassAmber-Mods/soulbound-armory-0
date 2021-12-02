package soulboundarmory.component.soulbound.item;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.tool.PickStorage;
import soulboundarmory.component.soulbound.item.weapon.DaggerStorage;
import soulboundarmory.component.soulbound.item.weapon.GreatswordStorage;
import soulboundarmory.component.soulbound.item.weapon.StaffStorage;
import soulboundarmory.component.soulbound.item.weapon.SwordStorage;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.registry.RegistryEntry;
import soulboundarmory.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

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

    public static StorageType<?> get(ResourceLocation id) {
        return registry().getValue(id);
    }

    public static StorageType<?> get(String name) {
        return get(new ResourceLocation(name));
    }

    public static List<ItemStorage<?>> storages(Entity entity) {
        return Components.soulbound(entity).flatMap(component -> component.storages().values().stream()).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static <T extends ItemStorage<T>> Optional<ItemStorage<T>> get(Entity entity, Item item) {
        return storages(entity).stream().filter(storage -> storage.item() == item).findAny().map(Util::cast);
    }

    public static Optional<ItemStorage<?>> firstSoulboundItem(Entity entity) {
        if (entity == null) {
            return Optional.empty();
        }

        for (var itemStack : entity.getHandSlots()) {
            var item = itemStack.getItem();
            var storage = Components.soulbound(entity).flatMap(component -> component.storages().values().stream()).filter(storage1 -> storage1.item() == item || storage1.canConsume(item)).findFirst();

            if (storage.isPresent()) {
                return storage;
            }
        }

        return Optional.empty();
    }

    public T get(Entity entity) {
        return Components.soulbound(entity).map(this::get).filter(Objects::nonNull).findAny().orElse(null);
    }

    public T get(SoulboundComponent component) {
        return component.storage(this);
    }

    @Override
    public String toString() {
        return "storage type " + this.getRegistryName();
    }
}
