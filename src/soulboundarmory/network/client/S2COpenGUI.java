package soulboundarmory.network.client;

import soulboundarmory.client.gui.screen.SoulboundTab;
import net.minecraftforge.fml.network.NetworkEvent;
import soulboundarmory.SoulboundArmoryClient;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.ItemComponentPacket;

public class S2COpenGUI implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        if (SoulboundArmoryClient.client.screen instanceof SoulboundTab) {
            storage.openGUI(buffer.readInt());
        }
    }
}
