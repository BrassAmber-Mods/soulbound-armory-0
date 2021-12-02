package soulboundarmory.enchantment;

import soulboundarmory.item.SoulboundStaffItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class ImpactEnchantment extends Enchantment {
    public ImpactEnchantment() {
        super(Rarity.COMMON, EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getMinEnchantability(int level) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return Integer.MAX_VALUE;
    }

    @Override
    public float calcDamageByCreature(int level, CreatureAttribute group) {
        return 1 + Math.max(0, level - 1) / 2F;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof SoulboundStaffItem;
    }
}
