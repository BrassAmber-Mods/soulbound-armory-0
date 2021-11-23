package net.auoeke.soulboundarmory.capability.statistics;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.function.Predicate;
import net.auoeke.soulboundarmory.serial.CompoundSerializable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistries;

public class EnchantmentStorage extends Object2ObjectOpenHashMap<Enchantment, Integer> implements Iterable<Enchantment>, CompoundSerializable {
    public EnchantmentStorage(Predicate<Enchantment> predicate) {
        ForgeRegistries.ENCHANTMENTS.getValues().stream().filter(predicate).forEach(enchantment -> this.put(enchantment, 0));
    }

    @Override
    public Integer get(Object enchantment) {
        var result = super.get(enchantment);

        return result == null ? 0 : result;
    }

    public void add(Enchantment enchantment, int value) {
        this.put(enchantment, this.get(enchantment) + value);
    }

    public void reset() {
        for (var enchantment : this) {
            this.put(enchantment, 0);
        }
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        var registry = Registry.ENCHANTMENT;

        for (var key : nbt.getAllKeys()) {
            var enchantment = registry.get(new ResourceLocation(key));

            if (this.containsKey(enchantment)) {
                this.put(enchantment, nbt.getInt(key));
            }
        }
    }

    @Override
    public void serializeNBT(CompoundNBT tag) {
        var registry = ForgeRegistries.ENCHANTMENTS;

        for (var enchantment : this) {
            var level = this.get(enchantment);

            if (level != null) {
                var identifier = registry.getKey(enchantment);

                if (identifier != null) {
                    tag.putInt(identifier.toString(), this.get(enchantment));
                }
            }
        }
    }

    @Override
    public Iterator<Enchantment> iterator() {
        return this.keySet().iterator();
    }
}
