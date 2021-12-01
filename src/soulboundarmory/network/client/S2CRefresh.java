package soulboundarmory.network.client;

import net.minecraftforge.fml.network.NetworkEvent;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.ItemComponentPacket;

public class S2CRefresh implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        storage.refresh();
    }
}
