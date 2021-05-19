package user11681.soulboundarmory.network.client;

import net.minecraftforge.fml.network.NetworkEvent;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundItemUtil;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.ItemComponentPacket;

public class S2CItemType implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        this.player().inventory.removeItemNoUpdate(this.player().inventory.selected);
        storage.removeOtherItems();
        storage.unlocked(true);

        SoulboundItemUtil.addItemStack(storage.getItemStack(), this.player());
        storage.sync();
        storage.refresh();
    }
}
