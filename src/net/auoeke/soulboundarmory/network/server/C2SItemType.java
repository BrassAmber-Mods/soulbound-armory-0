package net.auoeke.soulboundarmory.network.server;

import net.minecraftforge.fml.network.NetworkEvent;
import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.auoeke.soulboundarmory.network.ExtendedPacketBuffer;
import net.auoeke.soulboundarmory.network.ItemComponentPacket;

public class C2SItemType implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        this.player(context).inventory.setStack(buffer.readInt(), storage.itemStack());
        storage.getCapability().currentItem(storage);
        storage.removeOtherItems();
        storage.sync();
    }
}
