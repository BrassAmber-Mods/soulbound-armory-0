package net.auoeke.soulboundarmory.network.client;

import net.minecraftforge.fml.network.NetworkEvent;
import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.auoeke.soulboundarmory.capability.soulbound.player.SoulboundItemUtil;
import net.auoeke.soulboundarmory.network.ExtendedPacketBuffer;
import net.auoeke.soulboundarmory.network.ItemComponentPacket;

public class S2CItemType implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        this.player().inventory.removeItemNoUpdate(this.player().inventory.selected);
        storage.removeOtherItems();
        storage.unlocked(true);

        SoulboundItemUtil.addItemStack(storage.itemStack(), this.player());
        storage.sync();
        storage.refresh();
    }
}
