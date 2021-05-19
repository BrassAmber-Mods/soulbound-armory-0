package user11681.soulboundarmory.capability.soulbound.item;

import com.mojang.serialization.Lifecycle;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.capability.soulbound.item.tool.PickStorage;
import user11681.soulboundarmory.capability.soulbound.item.weapon.DaggerStorage;
import user11681.soulboundarmory.capability.soulbound.item.weapon.GreatswordStorage;
import user11681.soulboundarmory.capability.soulbound.item.weapon.StaffStorage;
import user11681.soulboundarmory.capability.soulbound.item.weapon.SwordStorage;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.util.Util;

public class StorageType<T extends ItemStorage<T>> {
    public static final Registry<StorageType<?>> registry = new SimpleRegistry<>(RegistryKey.ofRegistry(SoulboundArmory.id("storage_type")), Lifecycle.stable());

    public static final StorageType<DaggerStorage> dagger = register(new StorageType<>(), "dagger");
    public static final StorageType<SwordStorage> sword = register(new StorageType<>(), "sword");
    public static final StorageType<GreatswordStorage> greatsword = register(new StorageType<>(), "greatsword");
    public static final StorageType<StaffStorage> staff = register(new StorageType<>(), "staff");
    public static final StorageType<PickStorage> pick = register(new StorageType<>(), "pick");
    public static final Registry<StorageType<? extends ItemStorage<?>>> storage = Util.registry("storage");

    public static List<ItemStorage<?>> getStorages(Entity entity) {
        final List<ItemStorage<?>> storages = new ArrayList<>();

        for (SoulboundCapability component : Capabilities.get(entity)) {
            storages.addAll(component.storages().values());
        }

        return storages;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ItemStorage<T>> ItemStorage<T> get(Entity entity, final Item item) {
        for (ItemStorage<?> storage : getStorages(entity)) {
            if (storage.getItem() == item) {
                return (ItemStorage<T>) storage;
            }
        }

        return Util.nul();
    }

    public static ItemStorage<?> getFirstMenuStorage(Entity entity) {
        if (entity == null) {
            return null;
        }

        for (ItemStack itemStack : entity.getHandSlots()) {
            Item item = itemStack.getItem();

            for (SoulboundCapability component : Capabilities.get(entity)) {
                for (ItemStorage<?> storage : component.storages().values()) {
                    if (storage.getItem() == item || storage.canConsume(item)) {
                        return storage;
                    }
                }
            }
        }

        return null;
    }

    private static <T extends StorageType<?>> T register(T entry, String path) {
        return Registry.register(registry, SoulboundArmory.id(path), entry);
    }

    public ResourceLocation id() {
        return registry.getId(this);
    }

    @Override
    public String toString() {
        return this.id().toString();
    }

    public T get(Entity entity) {
        for (SoulboundCapability component : Capabilities.get(entity)) {
            T storage = this.get(component);

            if (storage != null) {
                return storage;
            }
        }

        return null;
    }

    public T get(SoulboundCapability component) {
        return component.storage(this);
    }
}
