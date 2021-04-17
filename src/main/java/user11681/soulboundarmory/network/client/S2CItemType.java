package user11681.soulboundarmory.network.client;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.player.SoulboundItemUtil;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;

public class S2CItemType implements ClientItemComponentPacket {
    @Override
    public void execute(MinecraftClient client, ClientPlayNetworkHandler handler, ExtendedPacketBuffer buffer, PacketSender responder, ItemStorage<?> storage) {
        client.player.getInventory().removeStack(client.player.getInventory().selectedSlot);
        storage.removeOtherItems();
        storage.setUnlocked(true);

        SoulboundItemUtil.addItemStack(storage.getItemStack(), client.player);
        storage.sync();
        storage.refresh();
    }
}
