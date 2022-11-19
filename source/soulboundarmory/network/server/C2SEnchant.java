package soulboundarmory.network.server;

import net.minecraftforge.registries.ForgeRegistries;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.network.ItemComponentPacket;

public final class C2SEnchant extends ItemComponentPacket {
	@Override
	public void execute(ItemComponent<?> component) {
		var enchantment = ForgeRegistries.ENCHANTMENTS.getValue(this.message.readIdentifier());
		var add = this.message.readBoolean();
		component.addEnchantment(enchantment, this.message.readBoolean() ? add ? Integer.MAX_VALUE : Integer.MIN_VALUE : add ? 1 : -1);
	}
}
