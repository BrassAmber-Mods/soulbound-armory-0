package user11681.soulboundarmory.network.client;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraftforge.fml.network.NetworkEvent;
import user11681.soulboundarmory.SoulboundArmoryClient;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.client.gui.screen.tab.SoulboundTab;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.ItemComponentPacket;

public class S2COpenGUI implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        if (SoulboundArmoryClient.client.screen instanceof SoulboundTab) {
            storage.openGUI(buffer.readInt());
        }
    }
}
