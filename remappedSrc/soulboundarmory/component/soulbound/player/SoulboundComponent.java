package soulboundarmory.component.soulbound.player;

import I;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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
        for (ItemStack itemStack : this.entity.getItemsHand()) {
            for (ItemStorage component : this.storages.values()) {
                if (itemStack.getItem() == component.item()) {
                    return component;
                }
            }
        }

        return null;
    }

    public ItemStorage<?> menuStorage() {
        for (ItemStorage component : this.storages.values()) {
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
            ItemStorage storage = this.heldItemStorage();

            if (storage == null) {
                storage = this.currentItem;
            } else {
                this.currentItem(storage);
            }

            if (storage != null) {
                Class baseItemClass = storage.itemClass();
                PlayerInventory inventory = this.entity.inventory;
                List combinedInventory = ItemUtil.inventory(this.entity);
                ItemStack newItemStack = storage.stack();
                I firstSlot = -1;

                for (ItemStack stack : combinedInventory) {
                    if (baseItemClass.isInstance(stack.getItem())) {
                        I index = combinedInventory.indexOf(stack);

                        if (stack.getItem() == storage.item() && (firstSlot == -1 || index == 36)) {
                            firstSlot = index == 36 ? 40 : index;

                            if (storage.boundSlot() != -1) {
                                storage.bindSlot(firstSlot);
                            }

                            NbtCompound tag = newItemStack.getTag();
                            newItemStack.setCustomName(stack.getName());

                            if (tag != null && !tag.equals(stack.getTag())) {
                                inventory.setStack(firstSlot, newItemStack);
                            }
                        } else if (!(this.entity.isCreative() || index == firstSlot && firstSlot == -1)) {
                            inventory.removeOne(stack);
                        }
                    }
                }
            }
        }

        for (ItemStorage storage : this.storages.values()) {
            storage.tick();
        }
    }

    @Override
    public void serialize(NbtCompound tag) {
        if (this.currentItem != null) {
            tag.putString("storage", this.currentItem.type().id().toString());
        }

        for (ItemStorage storage : this.storages.values()) {
            tag.put(storage.type().id().toString(), storage.serializeNBT());
        }
    }

    @Override
    public void deserialize(NbtCompound tag) {
        StorageType type = StorageType.get(tag.getString("storage"));

        if (type != null) {
            this.currentItem = this.storage(type);
        }

        for (ItemStorage storage : this.storages.values()) {
            storage.deserializeNBT(tag.getCompound(storage.type().id().toString()));
        }
    }
}
