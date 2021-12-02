package soulboundarmory.component.soulbound.player;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import soulboundarmory.component.EntityComponent;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.soulbound.item.StorageType;
import soulboundarmory.util.ItemUtil;

public abstract class SoulboundComponent extends EntityComponent<PlayerEntity> {
    protected final Map<StorageType<? extends ItemStorage<?>>, ItemStorage<?>> storages;

    protected ItemStorage<?> currentItem;

    public SoulboundComponent(PlayerEntity player) {
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
                if (itemStack.getItem() == component.item()) {
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
                var newItemStack = storage.stack();
                var firstSlot = -1;

                for (var stack : combinedInventory) {
                    if (baseItemClass.isInstance(stack.getItem())) {
                        var index = combinedInventory.indexOf(stack);

                        if (stack.getItem() == storage.item() && (firstSlot == -1 || index == 36)) {
                            firstSlot = index == 36 ? 40 : index;

                            if (storage.boundSlot() != -1) {
                                storage.bindSlot(firstSlot);
                            }

                            var tag = newItemStack.getTag();
                            newItemStack.setHoverName(stack.getHoverName());

                            if (tag != null && !tag.equals(stack.getTag())) {
                                inventory.setItem(firstSlot, newItemStack);
                            }
                        } else if (!(this.entity.isCreative() || index == firstSlot && firstSlot == -1)) {
                            inventory.removeItem(stack);
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
    public void serialize(CompoundNBT tag) {
        if (this.currentItem != null) {
            tag.putString("storage", this.currentItem.type().string());
        }

        for (var storage : this.storages.values()) {
            tag.put(storage.type().toString(), storage.serializeNBT());
        }
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        var type = StorageType.get(tag.getString("storage"));

        if (type != null) {
            this.currentItem = this.storage(type);
        }

        for (var storage : this.storages.values()) {
            storage.deserializeNBT(tag.getCompound(storage.type().toString()));
        }
    }
}
