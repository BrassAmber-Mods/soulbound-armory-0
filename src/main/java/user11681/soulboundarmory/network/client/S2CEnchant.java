package user11681.soulboundarmory.network.client;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.ItemComponentPacket;

public class S2CEnchant implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(buffer.readResourceLocation());

        storage.addEnchantment(enchantment, buffer.readInt());
        storage.refresh();
    }
}
