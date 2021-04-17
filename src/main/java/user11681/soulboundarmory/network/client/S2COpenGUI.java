package user11681.soulboundarmory.network.client;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import user11681.soulboundarmory.client.gui.screen.tab.SoulboundTab;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;

public class S2COpenGUI implements ClientItemComponentPacket {
    @Override
    public void execute(MinecraftClient client, ClientPlayNetworkHandler handler, ExtendedPacketBuffer buffer, PacketSender responder, ItemStorage<?> storage) {
        if (client.currentScreen instanceof SoulboundTab) {
            storage.openGUI(buffer.readInt());
        }
    }
}
