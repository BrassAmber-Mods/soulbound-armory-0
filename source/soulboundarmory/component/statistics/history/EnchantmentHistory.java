package soulboundarmory.component.statistics.history;

import net.minecraft.enchantment.Enchantment;
import soulboundarmory.component.soulbound.item.ItemComponent;

public final class EnchantmentHistory extends History<EnchantmentRecord> {
    public EnchantmentHistory(ItemComponent<?> component) {
        super(component);
    }

    public void record(Enchantment enchantment, int points) {
        this.record(new EnchantmentRecord(this.component, enchantment, points));
    }

    @Override
    protected EnchantmentRecord skeleton() {
        return new EnchantmentRecord(this.component);
    }
}
