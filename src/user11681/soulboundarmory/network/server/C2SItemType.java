package user11681.soulboundarmory.network.server;

import net.minecraftforge.fml.network.NetworkEvent;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.ItemComponentPacket;

public class C2SItemType implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        this.player(context).inventory.setStack(buffer.readInt(), storage.itemStack());
        storage.getCapability().currentItem(storage);
        storage.removeOtherItems();
        storage.sync();
    }
}
