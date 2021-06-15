package user11681.soulboundarmory.capability.soulbound.item;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.capability.soulbound.item.tool.PickStorage;
import user11681.soulboundarmory.capability.soulbound.item.weapon.DaggerStorage;
import user11681.soulboundarmory.capability.soulbound.item.weapon.GreatswordStorage;
import user11681.soulboundarmory.capability.soulbound.item.weapon.StaffStorage;
import user11681.soulboundarmory.capability.soulbound.item.weapon.SwordStorage;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.registry.RegistryEntry;
import user11681.soulboundarmory.util.Util;

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
    public static <T extends ItemStorage<T>> ItemStorage<T> get(Entity entity, Item item) {
        return (ItemStorage<T>) storages(entity).stream().filter(storage -> storage.getItem() == item).findAny().orElse(null);
    }

    public static ItemStorage<?> firstMenuStorage(Entity entity) {
        if (entity == null) {
            return null;
        }

        for (ItemStack itemStack : entity.getHandSlots()) {
            Item item = itemStack.getItem();

            return Capabilities.get(entity).flatMap(component -> component.storages().values().stream()).filter(storage -> storage.getItem() == item || storage.canConsume(item)).findAny().orElse(null);
        }

        return null;
    }

    public T get(Entity entity) {
        return Capabilities.get(entity).map(this::get).filter(Objects::nonNull).findAny().orElse(null);
    }

    public T get(SoulboundCapability component) {
        return component.storage(this);
    }
}
