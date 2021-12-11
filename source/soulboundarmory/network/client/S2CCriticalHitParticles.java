package soulboundarmory.network.client;

import cell.client.gui.CellElement;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.network.BufferPacket;

/**
 A server-to-client packet that is sent in order to display critical hit particles.
 <br><br>
 buffer: <br>
 - int (entity ID)
 */
public final class S2CCriticalHitParticles extends BufferPacket {
    @Override
    protected void execute() {
        var entity = this.message.readEntity();

        if (entity != null) {
            CellElement.minecraft.particleManager.addEmitter(entity, SoulboundArmory.criticalHitParticleType);
        }
    }
}
