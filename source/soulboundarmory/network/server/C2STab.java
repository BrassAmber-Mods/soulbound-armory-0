package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.player.MasterComponent;
import soulboundarmory.network.ComponentPacket;

public final class C2STab extends ComponentPacket {
    @Override
    protected void execute(MasterComponent<?> component) {
        component.tab(this.message.readByte());
    }
}
