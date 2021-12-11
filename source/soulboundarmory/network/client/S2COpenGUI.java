package soulboundarmory.network.client;

import cell.client.gui.CellElement;
import soulboundarmory.client.gui.screen.SoulboundTab;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.network.ItemComponentPacket;

public final class S2COpenGUI extends ItemComponentPacket {
    @Override
    public void execute(ItemComponent<?> storage) {
        if (CellElement.minecraft.currentScreen instanceof SoulboundTab) {
            // storage.openGUI(this.message.readInt());
        }
    }
}
