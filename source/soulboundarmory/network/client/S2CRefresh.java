package soulboundarmory.network.client;

import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.network.ComponentPacket;

/**
 A server-to-client packet that is sent in order to reinitialize the menu when new information has been reveived.

 <ul>buffer:
 <li>Identifier (component)</li>
 </ul>
 */
public final class S2CRefresh extends ComponentPacket {
    @Override
    protected void execute(SoulboundComponent<?> component) {
        component.refresh();
    }
}
