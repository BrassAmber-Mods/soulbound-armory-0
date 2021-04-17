package user11681.soulboundarmory.component.soulbound.player;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.item.StorageType;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.soulboundarmory.util.ItemUtil;

@SuppressWarnings("UnstableApiUsage")
public abstract class SoulboundComponent<T extends SoulboundComponent<T>> implements AutoSyncedComponent, PlayerComponent<T>, ServerTickingComponent {
    public final PlayerEntity player;

    protected final Map<StorageType<?>, ItemStorage<?>> storages;

    protected ItemStorage<?> currentItem;

    public SoulboundComponent(PlayerEntity player) {
        this.player = player;
        this.storages = new Object2ObjectOpenHashMap<>();
    }

    protected void store(ItemStorage<?> storage) {
        this.storages.put(storage.getType(), storage);
    }

    public ItemStorage<?> getStorage() {
        return this.currentItem;
    }

    public void setCurrentItem(final ItemStorage<?> storage) {
        this.currentItem = storage;

        storage.setUnlocked(true);
    }

    public ItemStorage<?> heldItemStorage() {
        for (ItemStack itemStack : this.player.getItemsHand()) {
            for (ItemStorage<?> component : this.storages.values()) {
                if (itemStack.getItem() == component.getItem()) {
                    return component;
                }
            }
        }

        return null;
    }

    public ItemStorage<?> menuStorage() {
        for (ItemStorage<?> component : this.storages.values()) {
            if (component.isAnyItemEquipped()) {
                return component;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public <S extends ItemStorage<S>> S getStorage(StorageType<S> type) {
        return (S) this.storages.get(type);
    }

    public Map<StorageType<?>, ItemStorage<?>> getStorages() {
        return this.storages;
    }

    public boolean hasSoulboundItem() {
        for (ItemStorage<?> storage : this.storages.values()) {
            if (ItemUtil.hasItem(this.player, storage.getBaseItemClass())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void serverTick() {
        if (this.hasSoulboundItem()) {
            ItemStorage<?> storage = this.heldItemStorage();

            if (storage != null) {
                this.setCurrentItem(storage);
            } else {
                storage = this.currentItem;
            }

            if (storage != null) {
                Class<? extends SoulboundItem> baseItemClass = storage.getBaseItemClass();
                PlayerInventory inventory = this.player.getInventory();
                List<ItemStack> combinedInventory = ItemUtil.inventory(this.player);
                ItemStack newItemStack = storage.getItemStack();
                int firstSlot = -1;

                for (ItemStack itemStack : combinedInventory) {
                    if (baseItemClass.isInstance(itemStack.getItem())) {
                        int index = combinedInventory.indexOf(itemStack);

                        if (itemStack.getItem() == storage.getItem() && (firstSlot == -1 || index == 36)) {
                            firstSlot = index == 36 ? 40 : index;

                            if (storage.getBoundSlot() != -1) {
                                storage.bindSlot(firstSlot);
                            }

                            NbtCompound tag = newItemStack.getTag();

                            newItemStack.setCustomName(itemStack.getName());

                            if (tag != null && !tag.equals(itemStack.getTag())) {
                                inventory.setStack(firstSlot, newItemStack);
                            }
                        } else if (!this.player.isCreative() && (index != firstSlot || firstSlot != -1)) {
                            inventory.removeOne(itemStack);
                        }
                    }
                }
            }
        }

        for (ItemStorage<?> storage : this.storages.values()) {
            storage.tick();
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        StorageType<?> type = StorageType.registry.get(SoulboundArmory.id(tag.getString("storage")));

        if (type != null) {
            this.currentItem = type.get(this);
        }

        for (ItemStorage<?> storage : this.storages.values()) {
            storage.fromTag(tag.getCompound(storage.getType().toString()));
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        if (this.currentItem != null) {
            tag.putString("storage", this.currentItem.getType().toString());
        }

        for (ItemStorage<?> storage : this.storages.values()) {
            tag.put(storage.getType().toString(), storage.toTag(new NbtCompound()));
        }
    }
}
