package soulboundarmory.network.client;

import net.minecraftforge.fml.network.NetworkEvent;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.soulbound.player.SoulboundItemUtil;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.ItemComponentPacket;

public class S2CItemType implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        this.player().inventory.removeItemNoUpdate(this.player().inventory.selected);
        storage.removeOtherItems();
        storage.unlocked(true);

        SoulboundItemUtil.addItemStack(storage.stack(), this.player());
        storage.sync();
        storage.refresh();
    }
}
