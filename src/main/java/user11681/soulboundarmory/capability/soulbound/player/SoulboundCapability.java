package user11681.soulboundarmory.capability.soulbound.player;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.soulboundarmory.serial.CompoundSerializable;
import user11681.soulboundarmory.util.ItemUtil;

public abstract class SoulboundCapability implements CompoundSerializable {
    public PlayerEntity player;

    protected final Map<StorageType<?>, ItemStorage<?>> storages;

    protected ItemStorage<?> currentItem;

    public SoulboundCapability() {
        this.storages = new Object2ObjectOpenHashMap<>();
    }

    protected void store(ItemStorage<?> storage) {
        this.storages.put(storage.getType(), storage);
    }

    public ItemStorage<?> storage() {
        return this.currentItem;
    }

    public void currentItem(ItemStorage<?> storage) {
        this.currentItem = storage;

        storage.unlocked(true);
    }

    public ItemStorage<?> heldItemStorage() {
        for (ItemStack itemStack : this.player.getHandSlots()) {
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
            if (component.anyItemEquipped()) {
                return component;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public <S extends ItemStorage<S>> S storage(StorageType<S> type) {
        return (S) this.storages.get(type);
    }

    public Map<StorageType<?>, ItemStorage<?>> storages() {
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

    public void tick() {
        if (this.hasSoulboundItem()) {
            ItemStorage<?> storage = this.heldItemStorage();

            if (storage != null) {
                this.currentItem(storage);
            } else {
                storage = this.currentItem;
            }

            if (storage != null) {
                Class<? extends SoulboundItem> baseItemClass = storage.getBaseItemClass();
                PlayerInventory inventory = this.player.inventory;
                List<ItemStack> combinedInventory = ItemUtil.inventory(this.player);
                ItemStack newItemStack = storage.getItemStack();
                int firstSlot = -1;

                for (ItemStack itemStack : combinedInventory) {
                    if (baseItemClass.isInstance(itemStack.getItem())) {
                        int index = combinedInventory.indexOf(itemStack);

                        if (itemStack.getItem() == storage.getItem() && (firstSlot == -1 || index == 36)) {
                            firstSlot = index == 36 ? 40 : index;

                            if (storage.boundSlot() != -1) {
                                storage.bindSlot(firstSlot);
                            }

                            CompoundNBT tag = newItemStack.getTag();

                            newItemStack.setHoverName(itemStack.getHoverName());

                            if (tag != null && !tag.equals(itemStack.getTag())) {
                                inventory.setItem(firstSlot, newItemStack);
                            }
                        } else if (!this.player.isCreative() && (index != firstSlot || firstSlot != -1)) {
                            inventory.removeItem(itemStack);
                        }
                    }
                }
            }
        }

        for (ItemStorage<?> storage : this.storages.values()) {
            storage.tick();
        }
    }

    public void deserializeNBT(CompoundNBT tag) {
        if (this.currentItem != null) {
            tag.putString("storage", this.currentItem.getType().toString());
        }

        for (ItemStorage<?> storage : this.storages.values()) {
            tag.put(storage.getType().toString(), storage.toTag(new CompoundNBT()));
        }
    }

    public void serializeNBT(CompoundNBT tag) {
        StorageType<?> type = StorageType.registry.get(SoulboundArmory.id((tag).getString("storage")));

        if (type != null) {
            this.currentItem = type.get(this);
        }

        for (ItemStorage<?> storage : this.storages.values()) {
            storage.fromTag(tag.getCompound(storage.getType().toString()));
        }
    }
}
