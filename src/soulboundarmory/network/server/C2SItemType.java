package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.network.ItemComponentPacket;

public class C2SItemType extends ItemComponentPacket {
    @Override
    public void execute(ItemStorage<?> storage) {
        this.player().inventory.setItem(this.buffer.readInt(), storage.stack());
        storage.component().currentItem(storage);
        storage.removeOtherItems();
        storage.sync();
    }
}
