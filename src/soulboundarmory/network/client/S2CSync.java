package soulboundarmory.network.client;

import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.ItemComponentPacket;
import net.minecraftforge.fml.network.NetworkEvent;
import soulboundarmory.component.soulbound.item.ItemStorage;

public class S2CSync implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        storage.deserializeNBT(buffer.readNbt());
    }
}
