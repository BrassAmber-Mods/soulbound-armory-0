package soulboundarmory.network.client;

import net.minecraft.nbt.NbtCompound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.network.ItemComponentPacket;

/**
 A server-to-client packet for updating the client's information about a soulbound item.
 <br><br>
 buffer: <br>
 - Identifier (item component type) <br>
 - NbtCompound ({@link ItemComponent#serialize(NbtCompound)}) <br>
 */
public final class S2CSyncItem extends ItemComponentPacket {
	@Override
	@OnlyIn(Dist.CLIENT)
	public void execute(ItemComponent<?> component) {
		component.deserialize(this.message.readNbt());
	}
}
