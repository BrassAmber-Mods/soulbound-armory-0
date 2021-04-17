package user11681.soulboundarmory.network.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;

@Environment(EnvType.CLIENT)
public interface ClientPacket extends ClientPlayNetworking.PlayChannelHandler {
    void execute(MinecraftClient client, ClientPlayNetworkHandler handler, ExtendedPacketBuffer buffer, PacketSender responder);

    @Override
    default void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buffer, PacketSender responder) {
        ExtendedPacketBuffer copy = ExtendedPacketBuffer.copy(buffer);

        client.execute(() -> this.execute(client, handler, copy, responder));
    }
}
