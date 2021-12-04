package soulboundarmory.network.client;

import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.network.ItemComponentPacket;

/**
 * A server-to-client packet that is sent in order to update the menu with new information.
 */
public class S2CRefresh extends ItemComponentPacket {
    @Override
    public void execute(ItemStorage<?> storage) {
        storage.refresh();
    }
}
