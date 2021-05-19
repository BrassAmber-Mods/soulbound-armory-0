package user11681.soulboundarmory.capability.statistics;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.function.Predicate;
import nerdhub.cardinal.components.api.util.INBTSerializable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

public class EnchantmentStorage extends Object2ObjectOpenHashMap<Enchantment, Integer> implements Iterable<Enchantment>, INBTSerializable {
    public EnchantmentStorage(Predicate<Enchantment> predicate) {
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (predicate.test(enchantment)) {
                this.put(enchantment, 0);
            }
        }
    }

    @Override
    public Integer get(Object enchantment) {
        Integer result = super.get(enchantment);

        return result == null ? 0 : result;
    }

    public void add(Enchantment enchantment, int value) {
        this.put(enchantment, this.get(enchantment) + value);
    }

    public void reset() {
        for (Enchantment enchantment : this) {
            this.put(enchantment, 0);
        }
    }

    @Override
    public void fromTag(CompoundNBT tag) {
        Registry<Enchantment> registry = Registry.ENCHANTMENT;

        for (String key : tag.getKeys()) {
            Enchantment enchantment = registry.get(new ResourceLocation(key));

            if (this.containsKey(enchantment)) {
                this.put(enchantment, tag.getInt(key));
            }
        }
    }

        @Override
    public CompoundNBT toTag(CompoundNBT tag) {
        Registry<Enchantment> registry = Registry.ENCHANTMENT;

        for (Enchantment enchantment : this) {
            Integer level = this.get(enchantment);

            if (level != null) {
                ResourceLocation identifier = registry.getId(enchantment);

                if (identifier != null) {
                    tag.putInt(identifier.toString(), this.get(enchantment));
                }
            }
        }

        return tag;
    }

    @Override
    public Iterator<Enchantment> iterator() {
        return this.keySet().iterator();
    }
}
