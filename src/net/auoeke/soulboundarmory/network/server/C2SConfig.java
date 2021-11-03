package net.auoeke.soulboundarmory.network.server;

import net.auoeke.soulboundarmory.network.ExtendedPacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.auoeke.soulboundarmory.capability.Capabilities;
import net.auoeke.soulboundarmory.network.SimplePacket;

public class C2SConfig implements SimplePacket {
    @Override
    public void execute(ExtendedPacketBuffer message, NetworkEvent.Context context) {
        Capabilities.config.get(this.player(context)).levelupNotifications = message.readBoolean();
    }
}
