package soulboundarmory.network.server;

import net.minecraftforge.fml.network.NetworkEvent;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.ItemComponentPacket;

public class C2SItemType implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        this.player(context).inventory.setItem(buffer.readInt(), storage.stack());
        storage.getCapability().currentItem(storage);
        storage.removeOtherItems();
        storage.sync();
    }
}
