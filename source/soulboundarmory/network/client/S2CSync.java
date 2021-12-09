package soulboundarmory.network.client;

import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.network.ItemComponentPacket;

/**
 * A server-to-client packet for updating the client's information about a soulbound item.
 */
public final class S2CSync extends ItemComponentPacket {
    @Override
    public void execute(ItemComponent<?> storage) {
        storage.deserialize(this.message.readNbt());
    }
}
