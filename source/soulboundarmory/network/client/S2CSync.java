package soulboundarmory.network.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import soulboundarmory.component.soulbound.player.MasterComponent;
import soulboundarmory.network.ComponentPacket;

/**
 A server-to-client packet that updates an entire soulbound component.
 <br><br>
 buffer: <br>
 - Identifier (component) <br>
 - NbtCompound (component) <br>
 */
public final class S2CSync extends ComponentPacket {
	@Override
	@OnlyIn(Dist.CLIENT)
	protected void execute(MasterComponent<?> component) {
		component.deserialize(this.message.readNbt());
	}
}
