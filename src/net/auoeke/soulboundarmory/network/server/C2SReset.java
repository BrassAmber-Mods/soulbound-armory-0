package net.auoeke.soulboundarmory.network.server;

import net.auoeke.soulboundarmory.capability.statistics.Category;
import net.auoeke.soulboundarmory.network.ExtendedPacketBuffer;
import net.auoeke.soulboundarmory.network.ItemComponentPacket;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.network.NetworkEvent;
import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;

public class C2SReset implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        Identifier identifier = buffer.readIdentifier();

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
