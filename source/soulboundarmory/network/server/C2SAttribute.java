package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.network.ItemComponentPacket;

public final class C2SAttribute extends ItemComponentPacket {
    @Override
    public void execute(ItemStorage<?> storage) {
        storage.incrementPoints(StatisticType.registry.getValue(this.message.readIdentifier()), this.message.readInt());
        storage.refresh();
    }
}
