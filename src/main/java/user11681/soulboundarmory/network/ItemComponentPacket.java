package user11681.soulboundarmory.network;

import net.minecraftforge.fml.network.NetworkEvent;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;

public interface ItemComponentPacket extends SimplePacket {
    void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage);

    @Override
    default void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context) {
        this.execute(buffer, context, StorageType.storage.get(buffer.readResourceLocation()).get(this.player(context)));
    }
}
