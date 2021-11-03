package net.auoeke.soulboundarmory.network;

import net.minecraftforge.fml.network.NetworkEvent;
import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.auoeke.soulboundarmory.capability.soulbound.item.StorageType;

public interface ItemComponentPacket extends SimplePacket {
    void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage);

    @Override
    default void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context) {
        this.execute(buffer, context, StorageType.get(buffer.readIdentifier()).get(this.player(context)));
    }
}
