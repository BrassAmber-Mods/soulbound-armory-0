package soulboundarmory.network.client;

import net.minecraftforge.registries.ForgeRegistries;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.network.ItemComponentPacket;

public class S2CEnchant extends ItemComponentPacket {
    @Override
    public void execute(ItemStorage<?> storage) {
        var enchantment = ForgeRegistries.ENCHANTMENTS.getValue(this.buffer.readResourceLocation());
        storage.addEnchantment(enchantment, this.buffer.readInt());
        storage.refresh();
    }
}
