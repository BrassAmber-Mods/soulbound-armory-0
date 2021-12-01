package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.ItemComponentPacket;
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
