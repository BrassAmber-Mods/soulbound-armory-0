package user11681.soulboundarmory.capability.soulbound.player;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import user11681.soulboundarmory.capability.EntityCapability;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.soulboundarmory.serial.CompoundSerializable;
import user11681.soulboundarmory.util.ItemUtil;

public abstract class SoulboundCapability extends EntityCapability<PlayerEntity> implements CompoundSerializable {
    protected final Map<StorageType<? extends ItemStorage<?>>, ItemStorage<?>> storages;

    protected ItemStorage<?> currentItem;

    public SoulboundCapability(PlayerEntity player) {
        super(player);

        this.storages = new Object2ObjectOpenHashMap<>();
    }

    public abstract boolean hasSoulboundItem();

    protected void store(ItemStorage<?> storage) {
        this.storages.put(storage.type(), storage);
    }

    public ItemStorage<?> storage() {
        return this.currentItem;
    }

    public void currentItem(ItemStorage<?> storage) {
        this.currentItem = storage;

        storage.unlocked(true);
    }

    public ItemStorage<?> heldItemStorage() {
        for (ItemStack itemStack : this.entity.getItemsHand()) {
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

    public <S extends ItemStorage<S>> S storage(StorageType<S> type) {
        return (S) this.storages.get(type);
    }

    public Map<StorageType<? extends ItemStorage<?>>, ItemStorage<?>> storages() {
        return this.storages;
    }

    public void tick() {
        if (this.hasSoulboundItem()) {
            ItemStorage<?> storage = this.heldItemStorage();

            if (storage == null) {
                storage = this.currentItem;
            } else {
                this.currentItem(storage);
            }

            if (storage != null) {
                Class<? extends SoulboundItem> baseItemClass = storage.itemClass();
                PlayerInventory inventory = this.entity.inventory;
                List<ItemStack> combinedInventory = ItemUtil.inventory(this.entity);
                ItemStack newItemStack = storage.itemStack();
                int firstSlot = -1;

                for (ItemStack itemStack : combinedInventory) {
                    if (baseItemClass.isInstance(itemStack.getItem())) {
                        int index = combinedInventory.indexOf(itemStack);

                        if (itemStack.getItem() == storage.getItem() && (firstSlot == -1 || index == 36)) {
                            firstSlot = index == 36 ? 40 : index;

                            if (storage.boundSlot() != -1) {
                                storage.bindSlot(firstSlot);
                            }

                            NbtCompound tag = newItemStack.getTag();
                            newItemStack.setCustomName(itemStack.getName());

                            if (tag != null && !tag.equals(itemStack.getTag())) {
                                inventory.setStack(firstSlot, newItemStack);
                            }
                        } else if (!this.entity.isCreative() && (index != firstSlot || firstSlot != -1)) {
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
    public void deserializeNBT(NbtCompound tag) {
        if (this.currentItem != null) {
            tag.putString("storage", this.currentItem.type().string());
        }

        for (ItemStorage<?> storage : this.storages.values()) {
            tag.put(storage.type().toString(), storage.serializeNBT());
        }
    }

    @Override
    public void serializeNBT(NbtCompound tag) {
        StorageType<?> type = StorageType.get(tag.getString("storage"));

        if (type != null) {
            this.currentItem = this.storage(type);
        }

        for (ItemStorage<?> storage : this.storages.values()) {
            storage.deserializeNBT(tag.getCompound(storage.type().toString()));
        }
    }
}
