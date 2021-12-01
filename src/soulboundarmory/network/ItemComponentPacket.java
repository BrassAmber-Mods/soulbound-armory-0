package soulboundarmory.network;

import net.minecraftforge.fml.network.NetworkEvent;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.soulbound.item.StorageType;

public interface ItemComponentPacket extends BufferPacket {
    void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage);

    @Override
    default void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context) {
        this.execute(buffer, context, StorageType.get(buffer.readResourceLocation()).get(this.player(context)));
    }
}
