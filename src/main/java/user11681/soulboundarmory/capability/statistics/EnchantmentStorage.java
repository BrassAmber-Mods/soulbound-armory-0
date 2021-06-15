package user11681.soulboundarmory.capability.statistics;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import user11681.soulboundarmory.serial.CompoundSerializable;

public class EnchantmentStorage extends Object2ObjectOpenHashMap<Enchantment, Integer> implements Iterable<Enchantment>, CompoundSerializable {
    public EnchantmentStorage(Predicate<Enchantment> predicate) {
        for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS) {
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
    public void deserializeNBT(CompoundNBT nbt) {
        Registry<Enchantment> registry = Registry.ENCHANTMENT;

        for (String key : nbt.getAllKeys()) {
            Enchantment enchantment = registry.get(new ResourceLocation(key));

            if (this.containsKey(enchantment)) {
                this.put(enchantment, nbt.getInt(key));
            }
        }
    }

    @Override
    public void serializeNBT(CompoundNBT tag) {
        IForgeRegistry<Enchantment> registry = ForgeRegistries.ENCHANTMENTS;

        for (Enchantment enchantment : this) {
            Integer level = this.get(enchantment);

            if (level != null) {
                ResourceLocation identifier = registry.getKey(enchantment);

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
