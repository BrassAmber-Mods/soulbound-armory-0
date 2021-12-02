package soulboundarmory.network.client;

import soulboundarmory.SoulboundArmoryClient;
import soulboundarmory.client.gui.screen.SoulboundTab;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.network.ItemComponentPacket;

public class S2COpenGUI extends ItemComponentPacket {
    @Override
    public void execute(ItemStorage<?> storage) {
        if (SoulboundArmoryClient.client.screen instanceof SoulboundTab) {
            storage.openGUI(this.buffer.readInt());
        }
    }
}
