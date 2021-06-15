package user11681.soulboundarmory.network.server;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.statistics.Category;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.ItemComponentPacket;

public class C2SReset implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        ResourceLocation identifier = buffer.readResourceLocation();

        if (identifier != null) {
            Category category = Category.registry.getValue(identifier);

            storage.reset(category);
        } else {
            storage.reset();
        }

        //        component.sync();
        storage.refresh();
    }
}
