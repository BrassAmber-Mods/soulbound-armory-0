package soulboundarmory.network.server;

import soulboundarmory.component.Components;
import soulboundarmory.network.BufferPacket;

/**
 * A client-to-server packet containing the client's configuration.
 */
public class C2SConfig extends BufferPacket {
    @Override
    public void execute() {
        Components.config.of(this.player()).levelupNotifications = this.buffer.readBoolean();
    }
}
