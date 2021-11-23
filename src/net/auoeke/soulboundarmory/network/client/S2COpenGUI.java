package net.auoeke.soulboundarmory.network.client;

import net.auoeke.soulboundarmory.client.gui.screen.SoulboundTab;
import net.minecraftforge.fml.network.NetworkEvent;
import net.auoeke.soulboundarmory.SoulboundArmoryClient;
import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.auoeke.soulboundarmory.network.ExtendedPacketBuffer;
import net.auoeke.soulboundarmory.network.ItemComponentPacket;

public class S2COpenGUI implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        if (SoulboundArmoryClient.client.screen instanceof SoulboundTab) {
            storage.openGUI(buffer.readInt());
        }
    }
}
