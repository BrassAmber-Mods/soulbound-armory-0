package soulboundarmory.component.soulbound.player;

import cell.client.gui.CellElement;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import soulboundarmory.client.gui.screen.SoulboundScreen;
import soulboundarmory.client.gui.screen.SoulboundTab;
import soulboundarmory.component.Component;
import soulboundarmory.component.ComponentKey;
import soulboundarmory.component.ComponentRegistry;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.soulbound.item.StorageType;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.util.ItemUtil;

public abstract class SoulboundComponent implements Component {
    public final Map<StorageType<? extends ItemStorage<?>>, ItemStorage<?>> storages = new Object2ObjectOpenHashMap<>();
    public final PlayerEntity player;
    public final boolean client;

    protected int tab;
    protected ItemStorage<?> storage;

    public SoulboundComponent(PlayerEntity player) {
        this.player = player;
        this.client = this.player.world.isClient;
    }

    public boolean hasSoulboundItem() {
        return ItemUtil.inventory(this.player).anyMatch(this::isAcceptable);
    }

    public int tab() {
        return this.tab;
    }

    public void tab(int tab) {
        this.tab = tab;

        if (this.client) {
            Packets.serverTab.send(new ExtendedPacketBuffer().writeIdentifier(this.key().id).writeByte(tab));
        }
    }

    public ItemStorage<?> item() {
        return this.storage;
    }

    public <S extends ItemStorage<S>> S item(StorageType<S> type) {
        return (S) this.storages.get(type);
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
            if (ItemUtil.handStacks(this.player).anyMatch(component::accepts)) {
                return component;
            }
        }

        return null;
    }

    public ItemStorage<?> menuStorage() {
        for (var component : this.storages.values()) {
            if (component.isMenuItemEquipped()) {
                return component;
            }
        }

        return null;
    }

    /**
     Open a GUI for this component if the player possesses a soulbound or {@linkplain ItemStorage#canConsume consumable} item.

     @return whether a GUI was opened.
     */
    public boolean tryOpenGUI() {
        var handStacks = ItemUtil.handStacks(this.player).toList().listIterator();

        while (handStacks.hasNext()) {
            var stack = handStacks.next();
            var match = this.storages.values().stream().filter(storage -> storage.accepts(stack)).findAny();
            var slot = handStacks.previousIndex();
            slot = slot == 1 ? 40 : this.player.inventory.selectedSlot;

            if (match.isPresent() || this.storages.values().stream().anyMatch(storage -> storage.canConsume(stack))) {
                CellElement.minecraft.openScreen(new SoulboundScreen(this, slot));

                return true;
            }
        }

        return false;
    }

    /**
     Reinitialize the current {@linkplain SoulboundScreen menu} if open.
     */
    public void refresh() {
        if (this.client) {
            if (CellElement.minecraft.currentScreen instanceof SoulboundScreen screen) {
                screen.refresh();
            }
        } else {
            Packets.clientRefresh.send(this.player, new ExtendedPacketBuffer(this));
        }
    }

    public void tick() {
        var storage = this.heldItemStorage();

        if (storage == null) {
            storage = this.storage;
        } else {
            this.currentItem(storage);
        }

        if (storage != null) {
            var inventory = this.player.inventory;
            var combinedInventory = ItemUtil.inventory(this.player).toList();
            var newItemStack = storage.stack();
            var found = false;
            // var firstSlot = -1;

            for (var iterator = combinedInventory.listIterator(); iterator.hasNext();) {
                var stack = iterator.next();

                if (this.isAcceptable(stack)) {
                    var index = iterator.previousIndex();

                    if (storage.accepts(stack) && !found) {
                        found = true;
                        // firstSlot = /*index == 36 ? 40 :*/ index;
                        var tag = newItemStack.getTag();

                        if (tag != null && !tag.equals(stack.getTag())) {
                            newItemStack.setCustomName(stack.getName());
                            inventory.setStack(index, newItemStack);
                        }
                    } else if (!this.player.isCreative()) {
                        inventory.removeOne(stack);
                    }
                }
            }
        }

        this.storages.values().forEach(ItemStorage::tick);
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
            this.storage = this.item(type);
        }

        for (var storage : this.storages.values()) {
            storage.deserializeNBT(tag.getCompound(storage.type().id().toString()));
        }

        this.tab = tag.getInt("tab");
    }

    /**
     @return this component's {@linkplain ComponentRegistry#register registered} {@linkplain ComponentKey key}.
     */
    public abstract ComponentKey<PlayerEntity, ? extends SoulboundComponent> key();

    public abstract SoulboundTab selectionTab();

    protected abstract boolean isAcceptable(ItemStack stack);
}
