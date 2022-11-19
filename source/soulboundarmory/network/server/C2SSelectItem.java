package soulboundarmory.network.server;

import soulboundarmory.client.gui.screen.SelectionTab;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.network.ItemComponentPacket;

/**
 A client-to-server packet that is sent when a client selects an item.

 @see SelectionTab */
public final class C2SSelectItem extends ItemComponentPacket {
	@Override
	protected void execute(ItemComponent<?> component) {
		component.select(this.message.readInt());
	}
}
