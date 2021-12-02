package soulboundarmory.network.server;

import I;
import Z;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.network.ItemComponentPacket;

public class C2SEnchant extends ItemComponentPacket {
    @Override
    public void execute(ItemStorage<?> storage) {
        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(this.message.readIdentifier());
        Z add = this.message.readBoolean();
        I change = add ? 1 : -1;

        if (this.message.readBoolean()) {
            change *= add ? storage.datum(StatisticType.enchantmentPoints) : storage.enchantment(enchantment);
        }

        storage.addEnchantment(enchantment, change);
        storage.refresh();
    }
}
