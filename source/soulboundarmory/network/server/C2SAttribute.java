package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.network.ItemComponentPacket;

/**
 A client-to-server packet that is sent when a client spends points on an attribute.
 <br><br>
 buffer:<br>
 - Identifier (item component type) <br>
 - Identifier (statistic type) <br>
 - int (points) <br>
 */
public final class C2SAttribute extends ItemComponentPacket {
    @Override
    public void execute(ItemComponent<?> component) {
        component.addAttribute(this.message.readRegistryEntry(StatisticType.registry), this.message.readInt());
    }
}
