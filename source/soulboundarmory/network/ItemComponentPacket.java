package soulboundarmory.network;

import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.soulbound.item.StorageType;

public abstract class ItemComponentPacket extends BufferPacket {
    protected abstract void execute(ItemStorage<?> storage);

    @Override
    protected final void execute() {
        this.execute(StorageType.get(this.message.readIdentifier()).get(this.player()));
    }
}
