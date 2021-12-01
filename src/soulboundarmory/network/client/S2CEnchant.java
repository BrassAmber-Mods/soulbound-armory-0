package soulboundarmory.network.client;

import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.ItemComponentPacket;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class S2CEnchant implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        var enchantment = ForgeRegistries.ENCHANTMENTS.getValue(buffer.readResourceLocation());
        storage.addEnchantment(enchantment, buffer.readInt());
        storage.refresh();
    }
}
