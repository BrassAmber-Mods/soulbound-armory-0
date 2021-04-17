package user11681.soulboundarmory.network.client;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;

public class S2CRefresh implements ClientItemComponentPacket {
    @Override
    public void execute(MinecraftClient client, ClientPlayNetworkHandler handler, ExtendedPacketBuffer buffer, PacketSender responder, ItemStorage<?> storage) {
        storage.refresh();
    }
}
