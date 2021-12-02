package soulboundarmory.network.server;

import net.minecraftforge.registries.ForgeRegistries;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.network.ItemComponentPacket;

public class C2SEnchant extends ItemComponentPacket {
    @Override
    public void execute(ItemStorage<?> storage) {
        var enchantment = ForgeRegistries.ENCHANTMENTS.getValue(this.buffer.readResourceLocation());
        var add = this.buffer.readBoolean();
        var change = add ? 1 : -1;

        if (this.buffer.readBoolean()) {
            change *= add ? storage.datum(StatisticType.enchantmentPoints) : storage.enchantment(enchantment);
        }

        storage.addEnchantment(enchantment, change);
        storage.refresh();
    }
}
