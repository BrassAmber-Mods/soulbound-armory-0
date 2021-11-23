package net.auoeke.soulboundarmory.network.server;

import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.auoeke.soulboundarmory.capability.statistics.Category;
import net.auoeke.soulboundarmory.network.ExtendedPacketBuffer;
import net.auoeke.soulboundarmory.network.ItemComponentPacket;
import net.minecraftforge.fml.network.NetworkEvent;

public class C2SReset implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        var identifier = buffer.readResourceLocation();

        if (identifier != null) {
            storage.reset(Category.registry.getValue(identifier));
        } else {
            storage.reset();
        }

        // component.sync();
        storage.refresh();
    }
}
