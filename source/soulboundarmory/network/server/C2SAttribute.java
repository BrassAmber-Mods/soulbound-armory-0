package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.network.ItemComponentPacket;

public final class C2SAttribute extends ItemComponentPacket {
    @Override
    public void execute(ItemComponent<?> storage) {
        storage.incrementPoints(StatisticType.registry.getValue(this.message.readIdentifier()), this.message.readInt());
        storage.component.refresh();
    }
}
