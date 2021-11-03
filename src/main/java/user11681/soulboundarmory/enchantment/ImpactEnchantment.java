package user11681.soulboundarmory.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import user11681.soulboundarmory.item.SoulboundStaffItem;

public class ImpactEnchantment extends Enchantment {
    public ImpactEnchantment() {
        super(Rarity.COMMON, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getMinPower(int level) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxPower(int level) {
        return Integer.MAX_VALUE;
    }

    @Override
    public float getAttackDamage(int level, EntityGroup group) {
        return 1 + Math.max(0, level - 1) / 2F;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof SoulboundStaffItem;
    }
}
