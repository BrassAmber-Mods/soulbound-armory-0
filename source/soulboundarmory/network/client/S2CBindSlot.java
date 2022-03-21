package soulboundarmory.network.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import soulboundarmory.component.soulbound.player.MasterComponent;
import soulboundarmory.network.ComponentPacket;

/**
 A server-to-client packet that is sent to update the client's bound slot.
 <br><br>
 buffer: <br>
 - Identifier (item component type) <br>
 - int (slot) <br>
 */
public final class S2CBindSlot extends ComponentPacket {
    @Override
    @OnlyIn(Dist.CLIENT)
    protected void execute(MasterComponent<?> component) {
        component.bindSlot(this.message.readInt());
    }
}
