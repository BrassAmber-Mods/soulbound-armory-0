package soulboundarmory.network.client;

import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.network.ItemComponentPacket;

public class S2CRefresh extends ItemComponentPacket {
    @Override
    public void execute(ItemStorage<?> storage) {
        storage.refresh();
    }
}
