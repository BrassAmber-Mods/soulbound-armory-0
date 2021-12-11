package soulboundarmory.network.server;

import net.minecraftforge.registries.ForgeRegistries;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.network.ItemComponentPacket;

public final class C2SEnchant extends ItemComponentPacket {
    @Override
    public void execute(ItemComponent<?> storage) {
        var enchantment = ForgeRegistries.ENCHANTMENTS.getValue(this.message.readIdentifier());
        var add = this.message.readBoolean();
        var change = add ? 1 : -1;

        if (this.message.readBoolean()) {
            change *= add ? storage.intValue(StatisticType.enchantmentPoints) : storage.enchantment(enchantment);
        }

        storage.addEnchantment(enchantment, change);
    }
}
