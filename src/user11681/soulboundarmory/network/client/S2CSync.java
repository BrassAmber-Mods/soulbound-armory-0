package user11681.soulboundarmory.network.client;

import net.minecraftforge.fml.network.NetworkEvent;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.ItemComponentPacket;

public class S2CSync implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        storage.deserializeNBT(buffer.readNbt());
    }
}
