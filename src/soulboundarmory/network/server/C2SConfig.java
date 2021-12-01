package soulboundarmory.network.server;

import soulboundarmory.network.ExtendedPacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import soulboundarmory.component.Components;
import soulboundarmory.network.BufferPacket;

public class C2SConfig implements BufferPacket {
    @Override
    public void execute(ExtendedPacketBuffer message, NetworkEvent.Context context) {
        Components.config.of(this.player(context)).levelupNotifications = message.readBoolean();
    }
}
