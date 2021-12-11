package soulboundarmory.component.soulbound.item;

import cell.client.gui.CellElement;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import soulboundarmory.lib.component.ItemStackComponent;
import soulboundarmory.util.Util;

public class ItemMarkerComponent implements ItemStackComponent<ItemMarkerComponent> {
    public final ItemStack stack;
    public ItemComponent<?> item;

    public ItemMarkerComponent(ItemStack stack) {
        this.stack = stack;

        if (Util.isClient()) {
            this.item = ItemComponent.get(CellElement.minecraft.player, stack).orElse(null);
        }
    }

    @Override
    public void serialize(NbtCompound tag) {
        if (this.item != null) {
            tag.putUuid("player", this.item.player.getUuid());
            tag.putString("item", this.item.type().string());
        }
    }

    @Override
    public void deserialize(NbtCompound tag) {
        if (!Util.isClient()) {
            this.item = ItemComponentType.get(tag.getString("item")).get(Util.server().getPlayerManager().getPlayer(tag.getUuid("player")));
        }
    }
}
