package soulboundarmory.component.soulbound.player;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import soulboundarmory.component.ComponentKey;
import soulboundarmory.component.EntityComponent;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.soulbound.item.StorageType;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.util.ItemUtil;

public abstract class SoulboundComponent extends EntityComponent<PlayerEntity> {
    protected final Map<StorageType<? extends ItemStorage<?>>, ItemStorage<?>> storages = new Object2ObjectOpenHashMap<>();
    protected final boolean client = this.entity.world.isClient;

    protected int tab;
    protected ItemStorage<?> storage;

    public SoulboundComponent(PlayerEntity player) {
        super(player);
    }

    public abstract boolean hasSoulboundItem();

    public int tab() {
        return this.tab;
    }

    public void tab(int tab) {
        this.tab = tab;

        if (this.client) {
            Packets.serverTab.send(new ExtendedPacketBuffer().writeIdentifier(this.key().id).writeByte(tab));
        }
    }

    public ItemStorage<?> storage() {
        return this.storage;
    }

    public void currentItem(ItemStorage<?> storage) {
        this.storage = storage;
        storage.unlocked(true);
    }

    protected void store(ItemStorage<?> storage) {
        this.storages.put(storage.type(), storage);
    }

    public ItemStorage<?> heldItemStorage() {
        for (var component : this.storages.values()) {
            if (ItemUtil.isEquipped(this.entity, component.item())) {
                return component;
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
                storage = this.storage;
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

        for (var storage : this.storages.values()) {
            storage.tick();
        }
    }

    @Override
    public void serialize(NbtCompound tag) {
        if (this.storage != null) {
            tag.putString("storage", this.storage.type().id().toString());
        }

        for (var storage : this.storages.values()) {
            tag.put(storage.type().id().toString(), storage.serializeNBT());
        }

        tag.putInt("tab", this.tab);
    }

    @Override
    public void deserialize(NbtCompound tag) {
        var type = StorageType.get(tag.getString("storage"));

        if (type != null) {
            this.storage = this.storage(type);
        }

        for (var storage : this.storages.values()) {
            storage.deserializeNBT(tag.getCompound(storage.type().id().toString()));
        }

        this.tab = tag.getInt("tab");
    }

    protected abstract ComponentKey<PlayerEntity, ? extends SoulboundComponent> key();
}
