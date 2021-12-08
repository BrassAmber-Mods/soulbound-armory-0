package soulboundarmory.network.client;

import net.minecraftforge.registries.ForgeRegistries;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.network.ItemComponentPacket;

public final class S2CEnchant extends ItemComponentPacket {
    @Override
    public void execute(ItemComponent<?> storage) {
        storage.addEnchantment(ForgeRegistries.ENCHANTMENTS.getValue(this.message.readIdentifier()), this.message.readInt());
        storage.component.refresh();
    }
}
