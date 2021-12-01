package soulboundarmory.network.server;

import soulboundarmory.network.ExtendedPacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.network.ItemComponentPacket;

public class C2SBindSlot implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        var slot = buffer.readInt();

        if (storage.boundSlot() == slot) {
            storage.unbindSlot();
        } else {
            storage.bindSlot(slot);
        }

        // this.component.sync();
        storage.refresh();
    }
}
