package user11681.soulboundarmory.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import user11681.soulboundarmory.item.StaffItem;

public class ImpactEnchantment extends Enchantment {
    public ImpactEnchantment() {
        super(Rarity.COMMON, EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getMinCost(int level) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxCost(int level) {
        return Integer.MAX_VALUE;
    }

    @Override
    public float getDamageBonus(int level, CreatureAttribute group) {
        return 1 + Math.max(0, level - 1) / 2F;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof StaffItem;
    }
}
