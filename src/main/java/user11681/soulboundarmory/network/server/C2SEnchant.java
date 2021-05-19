package user11681.soulboundarmory.network.server;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.statistics.StatisticType;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.ItemComponentPacket;

public class C2SEnchant implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(buffer.readResourceLocation());
        boolean add = buffer.readBoolean();
        int change = add ? 1 : -1;

        if (buffer.readBoolean()) {
            change *= add ? storage.getDatum(StatisticType.enchantmentPoints) : storage.enchantment(enchantment);
        }

        storage.addEnchantment(enchantment, change);
        storage.refresh();
    }
}
