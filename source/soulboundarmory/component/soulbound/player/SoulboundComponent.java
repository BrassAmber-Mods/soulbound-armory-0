package soulboundarmory.component.soulbound.player;

import cell.client.gui.CellElement;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import soulboundarmory.client.gui.screen.SoulboundScreen;
import soulboundarmory.client.gui.screen.SoulboundTab;
import soulboundarmory.lib.component.Component;
import soulboundarmory.lib.component.ComponentKey;
import soulboundarmory.lib.component.ComponentRegistry;
import soulboundarmory.lib.component.EntityComponentKey;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.util.ItemUtil;

public abstract class SoulboundComponent implements Component {
    public final Map<ItemComponentType<? extends ItemComponent<?>>, ItemComponent<?>> items = new Reference2ReferenceOpenHashMap<>();
    public final PlayerEntity player;

    protected ItemComponent<?> item;
    protected int tab;

    public SoulboundComponent(PlayerEntity player) {
        this.player = player;
    }

    /**
     @return this component's {@linkplain ComponentRegistry#register registered} {@linkplain EntityComponentKey key}.
     */
    public abstract ComponentKey<? extends SoulboundComponent> key();

    /**
     @return the selection tab for this component.
     */
    public abstract SoulboundTab selectionTab();

    /**
     @return whether the given item stack matches any of this component's items.
     */
    public abstract boolean accepts(ItemStack stack);

    public final boolean isClient() {
        return this.player.world.isClient;
    }

    public ItemComponent<?> item() {
        return this.item;
    }

    public <S extends ItemComponent<S>> S item(ItemComponentType<S> type) {
        return (S) this.items.get(type);
    }

    public void currentItem(ItemComponent<?> item) {
        this.item = item;
        item.unlock();
    }

    protected void store(ItemComponent<?> item) {
        this.items.put(item.type(), item);
    }

    public int tab() {
        return this.tab;
    }

    public void tab(int tab) {
        this.tab = tab;

        if (this.isClient()) {
            Packets.serverTab.send(new ExtendedPacketBuffer().writeIdentifier(this.key().id).writeByte(tab));
        }
    }

    /**
     @return the item component that matches `stack`.
     */
    public Optional<ItemComponent<?>> component(ItemStack stack) {
        return this.items.values().stream().filter(component -> component.accepts(stack)).findAny();
    }

    /**
     @return the item component corresponding to the first held item stack that matches this component.
     */
    public Optional<ItemComponent<?>> heldItemComponent() {
        return ItemUtil.handStacks(this.player).flatMap(stack -> this.component(stack).stream()).findFirst();
    }

    /**
     Open a GUI for this component if the player possesses a soulbound or {@linkplain ItemComponent#canConsume consumable} item.

     @return whether a GUI was opened.
     */
    public boolean tryOpenGUI() {
        var handStacks = ItemUtil.handStacks(this.player).toList().listIterator();

        while (handStacks.hasNext()) {
            var stack = handStacks.next();
            var match = this.items.values().stream().filter(storage -> storage.accepts(stack)).findAny();
            var slot = handStacks.previousIndex();
            slot = slot == 1 ? 40 : this.player.inventory.selectedSlot;

            if (match.isPresent() || this.items.values().stream().anyMatch(storage -> storage.canConsume(stack))) {
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
        if (this.isClient()) {
            if (CellElement.minecraft.currentScreen instanceof SoulboundScreen screen) {
                screen.refresh();
            }
        } else {
            Packets.clientRefresh.send(this.player, new ExtendedPacketBuffer(this));
        }
    }

    /**
     Invoked every tick.
     */
    public void tick() {
        var storage = this.heldItemComponent().orElse(null);

        if (storage == null) {
            storage = this.item;
        } else {
            this.currentItem(storage);
        }

        if (storage != null) {
            this.item.updateInventory(-1);
        }

        this.items.values().forEach(ItemComponent::tick);
    }

    @Override
    public void serialize(NbtCompound tag) {
        if (this.item != null) {
            tag.putString("storage", this.item.type().id().toString());
        }

        for (var storage : this.items.values()) {
            tag.put(storage.type().id().toString(), storage.serialize());
        }

        tag.putInt("tab", this.tab);
    }

    @Override
    public void deserialize(NbtCompound tag) {
        var type = ItemComponentType.get(tag.getString("storage"));

        if (type != null) {
            this.item = this.item(type);
        }

        for (var storage : this.items.values()) {
            storage.deserialize(tag.getCompound(storage.type().id().toString()));
        }

        this.tab = tag.getInt("tab");
    }
}
