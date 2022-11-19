package soulboundarmory.network.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import soulboundarmory.component.soulbound.player.MasterComponent;
import soulboundarmory.network.ComponentPacket;

/**
 A server-to-client packet that is sent in order to reinitialize the menu when new information has been reveived.

 <ul>buffer:
 <li>Identifier (component)</li>
 </ul>
 */
public final class S2CRefresh extends ComponentPacket {
	@Override
	@OnlyIn(Dist.CLIENT)
	protected void execute(MasterComponent<?> component) {
		component.refresh();
	}
}
