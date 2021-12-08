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
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.util.ItemUtil;

public abstract class SoulboundComponent implements Component {
    public final Map<ItemComponentType<? extends ItemComponent<?>>, ItemComponent<?>> storages = new Object2ObjectOpenHashMap<>();
    public final PlayerEntity player;
    public final boolean client;

    protected int tab;
    protected ItemComponent<?> storage;

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

    public ItemComponent<?> item() {
        return this.storage;
    }

    public <S extends ItemComponent<S>> S item(ItemComponentType<S> type) {
        return (S) this.storages.get(type);
    }

    public void currentItem(ItemComponent<?> storage) {
        this.storage = storage;
        storage.unlocked(true);
    }

    protected void store(ItemComponent<?> storage) {
        this.storages.put(storage.type(), storage);
    }

    public ItemComponent<?> heldItemStorage() {
        for (var component : this.storages.values()) {
            if (ItemUtil.handStacks(this.player).anyMatch(component::accepts)) {
                return component;
            }
        }

        return null;
    }

    /**
     Open a GUI for this component if the player possesses a soulbound or {@linkplain ItemComponent#canConsume consumable} item.

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
            this.storage.updateInventory(-1);

/*
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
                        // firstSlot = index == 36 ? 40 : index;
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
*/
        }

        this.storages.values().forEach(ItemComponent::tick);
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
        var type = ItemComponentType.get(tag.getString("storage"));

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
