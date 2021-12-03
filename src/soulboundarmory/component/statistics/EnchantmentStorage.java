package soulboundarmory.component.statistics;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistries;
import soulboundarmory.serial.CompoundSerializable;

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
    public void serializeNBT(NbtCompound tag) {
        for (var enchantment : this) {
            var level = this.get(enchantment);

            if (level != null) {
                var identifier = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);

                if (identifier != null) {
                    tag.putInt(identifier.toString(), this.get(enchantment));
                }
            }
        }
    }

    @Override
    public void deserializeNBT(NbtCompound nbt) {
        for (var key : nbt.getKeys()) {
            var enchantment = Registry.ENCHANTMENT.get(new Identifier(key));

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
