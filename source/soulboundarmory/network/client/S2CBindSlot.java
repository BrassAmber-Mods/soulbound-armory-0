package soulboundarmory.network.client;

import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.network.ItemComponentPacket;

/**
 A server-to-client packet that is sent to update the client's bound slot.
 <br><br>
 buffer: <br>
 - Identifier (item component type) <br>
 - int (slot) <br>
 */
public final class S2CBindSlot extends ItemComponentPacket {
    @Override
    protected void execute(ItemComponent<?> component) {
        component.bindSlot(this.message.readInt());
        component.component.refresh();
    }
}
