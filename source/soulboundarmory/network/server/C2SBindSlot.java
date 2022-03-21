package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.player.MasterComponent;
import soulboundarmory.network.ComponentPacket;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;

public final class C2SBindSlot extends ComponentPacket {
    @Override
    public void execute(MasterComponent<?> component) {
        var slot = this.message.readInt();

        if (component.boundSlot() == slot) {
            slot = -1;
        }

        component.bindSlot(slot);
        Packets.clientBindSlot.send(component.player, new ExtendedPacketBuffer(component).writeInt(slot));
    }
}
