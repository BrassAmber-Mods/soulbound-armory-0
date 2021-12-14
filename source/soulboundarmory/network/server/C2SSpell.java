package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.network.BufferPacket;

public final class C2SSpell extends BufferPacket {
    @Override
    protected void execute() {
        ItemComponentType.staff.of(this.player()).spell(this.message.readByte());
    }
}
