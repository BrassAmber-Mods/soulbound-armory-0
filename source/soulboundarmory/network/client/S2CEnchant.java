package soulboundarmory.network.client;

import net.minecraftforge.registries.ForgeRegistries;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.network.ItemComponentPacket;

public final class S2CEnchant extends ItemComponentPacket {
    @Override
    public void execute(ItemStorage<?> storage) {
        storage.addEnchantment(ForgeRegistries.ENCHANTMENTS.getValue(this.message.readIdentifier()), this.message.readInt());
        storage.refresh();
    }
}
