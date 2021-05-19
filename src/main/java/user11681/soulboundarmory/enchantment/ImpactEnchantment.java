package user11681.soulboundarmory.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlotType;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import user11681.soulboundarmory.item.StaffItem;

public class ImpactEnchantment extends Enchantment {
    public ImpactEnchantment() {
        super(Rarity.COMMON, EnchantmentTarget.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
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
    public float getAttackDamage(int level, final EntityGroup group) {
        return 1 + Math.max(0, level - 1) / 2F;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof StaffItem;
    }
}
