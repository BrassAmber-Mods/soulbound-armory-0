package soulboundarmory.component.statistics;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.function.Predicate;
import soulboundarmory.serial.CompoundSerializable;
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
        // Can't use getOrDefault here due to recursion.
        var level = super.get(enchantment);
        return level == null ? 0 : level;
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
    public void deserializeNBT(CompoundNBT nbt) {
        var registry = Registry.ENCHANTMENT;

        for (var key : nbt.keySet()) {
            var enchantment = registry.getOrDefault(new ResourceLocation(key));

            if (this.containsKey(enchantment)) {
                this.put(enchantment, nbt.getInt(key));
            }
        }
    }

    @Override
    public Iterator<Enchantment> iterator() {
        return this.keySet().iterator();
    }
}
