package user11681.soulboundarmory.network.server;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.config.ConfigComponent;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;

public class C2SConfig implements ServerPacket {
    @Override
    public void execute(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, ExtendedPacketBuffer buffer, PacketSender responder) {
        ConfigComponent component = Components.config.get(player);

        component.setAddToOffhand(buffer.readBoolean());
        component.setLevelupNotifications(buffer.readBoolean());
    }
}
