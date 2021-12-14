package soulboundarmory.item;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public enum SoulboundToolMaterial implements ToolMaterial {
    SOULBOUND;

    @Override
    public int getDurability() {
        return 0;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 2;
    }

    @Override
    public float getAttackDamage() {
        return 0;
    }

    @Override
    public int getMiningLevel() {
        return 3;
    }

    @Override
    public int getEnchantability() {
        return 0;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return null;
    }
}
