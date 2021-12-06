package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.item.StorageType;
import soulboundarmory.network.BufferPacket;

public final class C2SSpell extends BufferPacket {
    @Override
    protected void execute() {
        StorageType.staff.get(this.player()).spell(this.message.readByte());
    }
}
