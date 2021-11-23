package net.auoeke.soulboundarmory.capability.soulbound.item;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.auoeke.soulboundarmory.capability.Capabilities;
import net.auoeke.soulboundarmory.capability.soulbound.item.tool.PickStorage;
import net.auoeke.soulboundarmory.capability.soulbound.item.weapon.DaggerStorage;
import net.auoeke.soulboundarmory.capability.soulbound.item.weapon.GreatswordStorage;
import net.auoeke.soulboundarmory.capability.soulbound.item.weapon.StaffStorage;
import net.auoeke.soulboundarmory.capability.soulbound.item.weapon.SwordStorage;
import net.auoeke.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import net.auoeke.soulboundarmory.registry.RegistryEntry;
import net.auoeke.soulboundarmory.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

@SuppressWarnings("unchecked")
public class StorageType<T extends ItemStorage<T>> extends RegistryEntry<StorageType<T>> {
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
        return Capabilities.get(entity).flatMap(capability -> capability.storages().values().stream()).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static <T extends ItemStorage<T>> Optional<ItemStorage<T>> get(Entity entity, Item item) {
        return storages(entity).stream().filter(storage -> storage.getItem() == item).findAny().map(Util::cast);
    }

    public static Optional<ItemStorage<?>> firstMenuStorage(Entity entity) {
        if (entity == null) {
            return Optional.empty();
        }

        for (var itemStack : entity.getHandSlots()) {
            var item = itemStack.getItem();
            var storage = Capabilities.get(entity).flatMap(component -> component.storages().values().stream()).filter(storage1 -> storage1.getItem() == item || storage1.canConsume(item)).findAny();

            if (storage.isPresent()) {
                return storage;
            }
        }

        return Optional.empty();
    }

    public T get(Entity entity) {
        return Capabilities.get(entity).map(this::get).filter(Objects::nonNull).findAny().orElse(null);
    }

    public T get(SoulboundCapability component) {
        return component.storage(this);
    }
}
