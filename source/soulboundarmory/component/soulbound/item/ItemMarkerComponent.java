package soulboundarmory.component.soulbound.item;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import soulboundarmory.lib.component.Component;
import soulboundarmory.util.Util;

public class ItemMarkerComponent implements Component {
    public final ItemStack stack;
    public ItemComponent<?> item;

    public ItemMarkerComponent(ItemStack stack) {
        this.stack = stack;
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
        var server = Util.server();
        this.item = ItemComponentType.get(tag.getString("item")).get(server == null ? MinecraftClient.getInstance().player : server.getPlayerManager().getPlayer(tag.getUuid("player")));
    }
}
