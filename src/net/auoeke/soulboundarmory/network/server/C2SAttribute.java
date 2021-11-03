package net.auoeke.soulboundarmory.network.server;

import net.auoeke.soulboundarmory.capability.statistics.StatisticType;
import net.minecraftforge.fml.network.NetworkEvent;
import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.auoeke.soulboundarmory.network.ExtendedPacketBuffer;
import net.auoeke.soulboundarmory.network.ItemComponentPacket;

public class C2SAttribute implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        storage.incrementPoints(StatisticType.registry.getValue(buffer.readIdentifier()), buffer.readInt());
        storage.refresh();
    }
}
