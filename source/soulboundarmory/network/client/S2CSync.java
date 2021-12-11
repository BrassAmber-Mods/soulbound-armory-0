package soulboundarmory.network.client;

import soulboundarmory.component.soulbound.player.SoulboundComponent;
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
    protected void execute(SoulboundComponent<?> component) {
        component.deserialize(this.message.readNbt());
    }
}
