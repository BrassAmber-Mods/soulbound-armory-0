package soulboundarmory.component.soulbound.player;

import cell.client.gui.screen.CellScreen;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceLinkedOpenHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.world.GameRules;
import soulboundarmory.client.gui.screen.SelectionTab;
import soulboundarmory.client.gui.screen.SoulboundScreen;
import soulboundarmory.client.gui.screen.SoulboundTab;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.config.Configuration;
import soulboundarmory.lib.component.ComponentRegistry;
import soulboundarmory.lib.component.EntityComponent;
import soulboundarmory.lib.component.EntityComponentKey;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.util.ItemUtil;
import soulboundarmory.util.Util;

public abstract class SoulboundComponent<C extends SoulboundComponent<C>> implements EntityComponent<C> {
    public final Map<ItemComponentType<? extends ItemComponent<?>>, ItemComponent<?>> items = new Reference2ReferenceLinkedOpenHashMap<>();
    public final PlayerEntity player;

    /**
     The index of the last open tab in the menu for that it may be restored when the menu is next opened.
     */
    public int tab;

    protected int boundSlot;
    protected ItemComponent<?> item;

    public SoulboundComponent(PlayerEntity player) {
        this.player = player;
    }

    public static Optional<? extends SoulboundComponent<?>> of(Entity entity, ItemStack stack) {
        return Components.soulbound(entity).filter(component -> component.accepts(stack)).findAny();
    }

    /**
     @return this component's {@linkplain ComponentRegistry#entity registered} {@linkplain EntityComponentKey key}.
     */
    public abstract EntityComponentKey<? extends SoulboundComponent<?>> key();

    /**
     @return whether the given item stack matches any of this component's items.
     */
    public abstract boolean accepts(ItemStack stack);

    /**
     @return what the name suggests.
     */
    public final boolean isClient() {
        return this.player.world.isClient;
    }

    public void tab(int index) {
        this.tab = index;

        if (this.isClient()) {
            Packets.serverTab.send(new ExtendedPacketBuffer(this).writeByte(index));
        }
    }

    public int boundSlot() {
        return this.boundSlot;
    }

    public void bindSlot(int boundSlot) {
        this.boundSlot = boundSlot;
    }

    public boolean hasBoundSlot() {
        return this.boundSlot != -1;
    }

    public void unbindSlot() {
        this.boundSlot = -1;
    }

    /**
     @return the item stack in the bound slot.
     @throws IndexOutOfBoundsException if no slot is bound.
     */
    public final ItemStack stackInBoundSlot() {
        return this.player.getInventory().getStack(this.boundSlot);
    }

    /**
     @return the active soulbound item component.
     */
    public ItemComponent<?> item() {
        return this.item;
    }

    /**
     Find this component's item component of the given type.

     @param type the item component type
     @param <S>  the class of the item component
     @return the item component if it exists or null.
     */
    public <S extends ItemComponent<S>> S item(ItemComponentType<S> type) {
        return (S) this.items.get(type);
    }

    /**
     Set the currently active soulbound item and unlock it if it is locked.

     @param item the item's component.
     */
    public void select(ItemComponent<?> item) {
        this.item = item;
        item.unlock();
    }

    /**
     @return the selection tab for this component.
     */
    public SoulboundTab selectionTab() {
        return new SelectionTab();
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
    public boolean tryOpenGUI(Hand hand) {
        var stack = this.player.getStackInHand(hand);
        var match = this.items.values().stream().filter(storage -> storage.accepts(stack)).findAny();
        var slot = hand == Hand.OFF_HAND ? 40 : this.player.getInventory().selectedSlot;

        if (match.isPresent() || this.items.values().stream().anyMatch(storage -> storage.canConsume(stack))) {
            new SoulboundScreen(this, slot).open();

            return true;
        }

        return false;
    }

    /**
     Reinitialize the current {@linkplain SoulboundScreen menu} if open.
     */
    public void refresh() {
        if (this.isClient()) {
            if (CellScreen.cellScreen() instanceof SoulboundScreen screen) {
                screen.refresh();
            }
        } else {
            Packets.clientRefresh.send(this.player, new ExtendedPacketBuffer(this));
        }
    }

    @Override
    public void tickStart() {
        var storage = this.heldItemComponent().orElse(null);

        if (storage == null) {
            storage = this.item;
        } else {
            this.select(storage);
        }

        if (storage != null) {
            this.item.updateInventory(this.hasBoundSlot() && this.accepts(this.stackInBoundSlot()) ? this.boundSlot : -1);
        }

        this.items.values().forEach(ItemComponent::tick);
    }

    /**
     Add an item component to this component.

     @param item the item component
     */
    protected void store(ItemComponent<?> item) {
        this.items.put(item.type(), item);
    }

    /**
     Update item components' item stacks and synchronize them.
     */
    @Override
    public void spawn() {
        if (!this.isClient()) {
            this.items.values().forEach(ItemComponent::updateItemStack);
            Packets.clientSync.send(this.player, new ExtendedPacketBuffer(this).writeNbt(this.serialize()));
        }
    }

    @Override
    public void copy(C copy) {
        EntityComponent.super.copy(copy);

        if (!this.player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            var item = this.item();

            if (item != null && item.level() >= Configuration.instance().preservationLevel) {
                this.player.getInventory().insertStack(item.stack());
            }
        }
    }

    @Override
    public void serialize(NbtCompound tag) {
        if (this.item != null) {
            tag.putString("item", this.item.type().id().toString());
        }

        for (var storage : this.items.values()) {
            tag.put(storage.type().id().toString(), storage.serialize());
        }

        tag.putInt("tab", this.tab);
        tag.putInt("slot", this.boundSlot);
    }

    @Override
    public void deserialize(NbtCompound tag) {
        var type = ItemComponentType.get(tag.getString("item"));

        if (type != null) {
            this.item = this.item(type);
        }

        for (var item : this.items.values()) {
            Util.ifPresent(tag, item.type().string(), item::deserialize);
        }

        this.tab = tag.getInt("tab");
        this.bindSlot(tag.getInt("slot"));
    }
}
