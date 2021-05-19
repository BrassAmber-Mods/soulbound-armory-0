package user11681.soulboundarmory.network.server;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.SoulboundArmory.channel;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;

public interface ServerPacket extends SoulboundArmory.channel.PlayChannelHandler {
    void execute(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, ExtendedPacketBuffer buffer, PacketSender responder);

    @Override
    default void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buffer, PacketSender responder) {
        ExtendedPacketBuffer copy = ExtendedPacketBuffer.copy(buffer);

        server.execute(() -> this.execute(server, player, handler, copy, responder));
    }
}
