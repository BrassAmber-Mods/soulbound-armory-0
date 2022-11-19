package soulboundarmory.network.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.network.ItemComponentPacket;

/**
 A server-to-client packet that updates the client after the server has changed the level of an enchantment.
 <br><br>
 buffer: <br>
 - Identifier (item component type) <br>
 - Identifier (enchantment) <br>
 - int (enchentment level) <br>
 - int (remaining enchantment points) <br>

 @see ItemComponent#addEnchantment */
public final class S2CEnchant extends ItemComponentPacket {
	@Override
	@OnlyIn(Dist.CLIENT)
	public void execute(ItemComponent<?> component) {
		component.enchantments.put(this.message.readRegistryEntry(ForgeRegistries.ENCHANTMENTS), this.message.readInt());
		component.set(StatisticType.enchantmentPoints, this.message.readInt());
	}
}
