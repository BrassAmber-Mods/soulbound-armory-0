package soulboundarmory.network.client;

import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.network.ItemComponentPacket;

/**
 A server-to-client packet that is sent to confirm
 */
public final class S2CBindSlot extends ItemComponentPacket {
    @Override
    protected void execute(ItemComponent<?> storage) {
        storage.bindSlot(this.message.readInt());
        storage.component.refresh();
    }
}
