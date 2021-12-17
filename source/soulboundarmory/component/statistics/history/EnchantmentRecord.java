package soulboundarmory.component.statistics.history;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.ForgeRegistries;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.config.Configuration;

public final class EnchantmentRecord extends Record {
    private Enchantment enchantment;
    private int points;

    public EnchantmentRecord(ItemComponent<?> component, Enchantment enchantment, int points) {
        super(component);

        this.enchantment = enchantment;
        this.points = points;
    }

    public EnchantmentRecord(ItemComponent<?> component) {
        super(component);
    }

    @Override
    public boolean revert(int level) {
        var interval = Configuration.instance().levelsPerEnchantment;
        var change = this.component.level() / interval - level / interval;
        var deduction = Math.min(this.points, change);
        this.component.addEnchantment(this.enchantment, -deduction);
        this.points -= deduction;

        return this.points + deduction >= change;
    }

    @Override
    public void pop() {
        this.component.addEnchantment(this.enchantment, -this.points);
    }

    @Override
    public void deserialize(NbtCompound tag) {
        tag.putString("enchantment", ForgeRegistries.ENCHANTMENTS.getKey(this.enchantment).toString());
        tag.putInt("points", this.points);
    }

    @Override
    public void serialize(NbtCompound tag) {
        this.enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new Identifier(tag.getString("enchantment")));
        this.points = tag.getInt("points");
    }
}
