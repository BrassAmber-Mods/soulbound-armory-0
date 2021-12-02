package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.network.ItemComponentPacket;

public class C2SBindSlot extends ItemComponentPacket {
    @Override
    public void execute(ItemStorage<?> storage) {
        var slot = this.message.readInt();

        if (storage.boundSlot() == slot) {
            storage.unbindSlot();
        } else {
            storage.bindSlot(slot);
        }

        // this.component.sync();
        storage.refresh();
    }
}
