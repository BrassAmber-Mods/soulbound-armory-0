package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.ItemComponentPacket;
import soulboundarmory.network.Packets;

public final class C2SBindSlot extends ItemComponentPacket {
    @Override
    public void execute(ItemComponent<?> storage) {
        var slot = this.message.readInt();

        if (storage.boundSlot() == slot) {
            slot = -1;
        }

        storage.bindSlot(slot);
        Packets.clientBindSlot.send(storage.player, new ExtendedPacketBuffer(storage).writeInt(slot));
    }
}
