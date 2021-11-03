package net.auoeke.soulboundarmory.network.client;

import net.auoeke.soulboundarmory.network.ExtendedPacketBuffer;
import net.auoeke.soulboundarmory.network.ItemComponentPacket;
import net.minecraftforge.fml.network.NetworkEvent;
import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;

public class S2CSync implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        storage.deserializeNBT(buffer.readNbt());
    }
}
