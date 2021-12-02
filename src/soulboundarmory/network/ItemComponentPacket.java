package soulboundarmory.network;

import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.soulbound.item.StorageType;

public abstract class ItemComponentPacket extends BufferPacket {
    public abstract void execute(ItemStorage<?> storage);

    @Override
    public void execute() {
        this.execute(StorageType.get(this.buffer.readResourceLocation()).get(this.player()));
    }
}
