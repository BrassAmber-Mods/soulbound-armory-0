package soulboundarmory.network.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.module.gui.widget.Widget;
import soulboundarmory.network.BufferPacket;

/**
 A server-to-client packet that is sent in order to display critical hit particles.
 <br><br>
 buffer: <br>
 - int (entity ID)
 */
public final class S2CCriticalHitParticles extends BufferPacket {
	@Override
	@OnlyIn(Dist.CLIENT)
	protected void execute() {
		this.message.readEntity().ifPresent(value -> Widget.client.particleManager.addEmitter(value, SoulboundArmory.criticalHitParticleType));
	}
}
