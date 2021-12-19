package soulboundarmory.network.client;

import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.network.ComponentPacket;

/**
 A server-to-client packet that is sent to update the client's bound slot.
 <br><br>
 buffer: <br>
 - Identifier (item component type) <br>
 - int (slot) <br>
 */
public final class S2CBindSlot extends ComponentPacket {
    @Override
    protected void execute(SoulboundComponent<?> component) {
        component.bindSlot(this.message.readInt());
        component.refresh();
    }
}
