package soulboundarmory.network.server;

import soulboundarmory.component.statistics.StatisticType;
import net.minecraftforge.fml.network.NetworkEvent;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.ItemComponentPacket;

public class C2SAttribute implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        storage.incrementPoints(StatisticType.registry.getValue(buffer.readResourceLocation()), buffer.readInt());
        storage.refresh();
    }
}
