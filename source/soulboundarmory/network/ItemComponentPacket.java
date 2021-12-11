package soulboundarmory.network;

import soulboundarmory.component.soulbound.item.ItemComponent;

public abstract class ItemComponentPacket extends BufferPacket {
    protected abstract void execute(ItemComponent<?> storage);

    @Override
    protected final void execute() {
        this.execute(this.message.readItemComponent(this.player()));
    }
}
