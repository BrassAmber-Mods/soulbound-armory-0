package soulboundarmory.network.client;

import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.soulbound.player.SoulboundItemUtil;
import soulboundarmory.network.ItemComponentPacket;

public class S2CItemType extends ItemComponentPacket {
    @Override
    public void execute(ItemStorage<?> storage) {
        this.player().inventory.removeItemNoUpdate(this.player().inventory.selected);
        storage.removeOtherItems();
        storage.unlocked(true);

        SoulboundItemUtil.addItemStack(storage.stack(), this.player());
        storage.sync();
        storage.refresh();
    }
}
