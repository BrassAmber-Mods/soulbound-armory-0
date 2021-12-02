package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.network.ItemComponentPacket;

public class C2SAttribute extends ItemComponentPacket {
    @Override
    public void execute(ItemStorage<?> storage) {
        storage.incrementPoints(StatisticType.registry.getValue(this.buffer.readResourceLocation()), this.buffer.readInt());
        storage.refresh();
    }
}
