package net.auoeke.soulboundarmory.network.client;

import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.auoeke.soulboundarmory.network.ExtendedPacketBuffer;
import net.auoeke.soulboundarmory.network.ItemComponentPacket;
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
