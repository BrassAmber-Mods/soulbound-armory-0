package soulboundarmory.network;

import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.item.ItemComponentType;

public abstract class ItemComponentPacket extends BufferPacket {
    protected abstract void execute(ItemComponent<?> storage);

    @Override
    protected final void execute() {
        this.execute(ItemComponentType.get(this.message.readIdentifier()).get(this.player()));
    }
}
