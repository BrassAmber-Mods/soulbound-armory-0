package net.auoeke.soulboundarmory.capability.soulbound.player;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.auoeke.soulboundarmory.capability.EntityCapability;
import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.auoeke.soulboundarmory.capability.soulbound.item.StorageType;
import net.auoeke.soulboundarmory.serial.CompoundSerializable;
import net.auoeke.soulboundarmory.util.ItemUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

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
        for (var itemStack : this.entity.getHandSlots()) {
            for (var component : this.storages.values()) {
                if (itemStack.getItem() == component.getItem()) {
                    return component;
                }
            }
        }

        return null;
    }

    public ItemStorage<?> menuStorage() {
        for (var component : this.storages.values()) {
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
            var storage = this.heldItemStorage();

            if (storage == null) {
                storage = this.currentItem;
            } else {
                this.currentItem(storage);
            }

            if (storage != null) {
                var baseItemClass = storage.itemClass();
                var inventory = this.entity.inventory;
                var combinedInventory = ItemUtil.inventory(this.entity);
                var newItemStack = storage.itemStack();
                var firstSlot = -1;

                for (var itemStack : combinedInventory) {
                    if (baseItemClass.isInstance(itemStack.getItem())) {
                        var index = combinedInventory.indexOf(itemStack);

                        if (itemStack.getItem() == storage.getItem() && (firstSlot == -1 || index == 36)) {
                            firstSlot = index == 36 ? 40 : index;

                            if (storage.boundSlot() != -1) {
                                storage.bindSlot(firstSlot);
                            }

                            var tag = newItemStack.getTag();
                            newItemStack.setHoverName(itemStack.getDisplayName());

                            if (tag != null && !tag.equals(itemStack.getTag())) {
                                inventory.setItem(firstSlot, newItemStack);
                            }
                        } else if (!this.entity.isCreative() && (index != firstSlot || firstSlot != -1)) {
                            inventory.removeItem(itemStack);
                        }
                    }
                }
            }
        }

        for (var storage : this.storages.values()) {
            storage.tick();
        }
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        if (this.currentItem != null) {
            tag.putString("storage", this.currentItem.type().string());
        }

        for (var storage : this.storages.values()) {
            tag.put(storage.type().toString(), storage.serializeNBT());
        }
    }

    @Override
    public void serializeNBT(CompoundNBT tag) {
        var type = StorageType.get(tag.getString("storage"));

        if (type != null) {
            this.currentItem = this.storage(type);
        }

        for (var storage : this.storages.values()) {
            storage.deserializeNBT(tag.getCompound(storage.type().toString()));
        }
    }
}
