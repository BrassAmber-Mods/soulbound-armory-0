package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.network.ComponentPacket;

public final class C2STab extends ComponentPacket {
    @Override
    protected void execute(SoulboundComponent component) {
        component.tab(this.message.readByte());
    }
}
