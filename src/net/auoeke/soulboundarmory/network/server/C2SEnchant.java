package net.auoeke.soulboundarmory.network.server;

import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.auoeke.soulboundarmory.capability.statistics.StatisticType;
import net.auoeke.soulboundarmory.network.ExtendedPacketBuffer;
import net.auoeke.soulboundarmory.network.ItemComponentPacket;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class C2SEnchant implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        var enchantment = ForgeRegistries.ENCHANTMENTS.getValue(buffer.readResourceLocation());
        var add = buffer.readBoolean();
        var change = add ? 1 : -1;

        if (buffer.readBoolean()) {
            change *= add ? storage.datum(StatisticType.enchantmentPoints) : storage.enchantment(enchantment);
        }

        storage.addEnchantment(enchantment, change);
        storage.refresh();
    }
}
