package net.auoeke.soulboundarmory.network.server;

import net.auoeke.soulboundarmory.network.ExtendedPacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.auoeke.soulboundarmory.network.ItemComponentPacket;

public class C2SBindSlot implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        int slot = buffer.readInt();

        if (storage.boundSlot() == slot) {
            storage.unbindSlot();
        } else {
            storage.bindSlot(slot);
        }

        //        this.component.sync();
        storage.refresh();
    }
}
