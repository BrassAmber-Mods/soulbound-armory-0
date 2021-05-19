package user11681.soulboundarmory.network.server;

import net.minecraftforge.fml.network.NetworkEvent;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.SimplePacket;

public class C2SConfig implements SimplePacket {
    @Override
    public void execute(ExtendedPacketBuffer message, NetworkEvent.Context context) {
        Capabilities.config.get(this.player(context)).levelupNotifications = message.readBoolean();
    }
}
