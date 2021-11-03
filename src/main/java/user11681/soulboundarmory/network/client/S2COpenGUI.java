package user11681.soulboundarmory.network.client;

import net.minecraftforge.fml.network.NetworkEvent;
import user11681.soulboundarmory.SoulboundArmoryClient;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.client.gui.screen.SoulboundTab;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.ItemComponentPacket;

public class S2COpenGUI implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        if (SoulboundArmoryClient.client.currentScreen instanceof SoulboundTab) {
            storage.openGUI(buffer.readInt());
        }
    }
}
