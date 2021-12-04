package soulboundarmory.item;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraftforge.common.util.Lazy;

public enum SoulboundToolMaterial implements ToolMaterial {
    SOULBOUND(0, 0, 0.5F, 0, 0, null);

    private final int miningLevel;
    private final int uses;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Lazy<Ingredient> repairIngredient;

    SoulboundToolMaterial(int miningLevel, int uses, float miningSpeed, float attackDamage, int enchantability, Lazy<Ingredient> repairIngredient) {
        this.miningLevel = miningLevel;
        this.uses = uses;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurability() {
        return this.uses;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeed;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
