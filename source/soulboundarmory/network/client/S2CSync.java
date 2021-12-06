package soulboundarmory.network.client;

import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.network.ItemComponentPacket;

/**
 * A server-to-client packet for updating the client's information about a soulbound item.
 */
public final class S2CSync extends ItemComponentPacket {
    @Override
    public void execute(ItemStorage<?> storage) {
        storage.deserializeNBT(this.message.readNbt());
    }
}
