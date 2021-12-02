package soulboundarmory.network.client;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.network.ItemComponentPacket;

public class S2CEnchant extends ItemComponentPacket {
    @Override
    public void execute(ItemStorage<?> storage) {
        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(this.message.readIdentifier());
        storage.addEnchantment(enchantment, this.message.readInt());
        storage.refresh();
    }
}
