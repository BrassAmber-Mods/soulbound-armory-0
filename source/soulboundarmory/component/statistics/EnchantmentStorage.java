package soulboundarmory.component.statistics;

import java.util.Iterator;
import java.util.function.Predicate;
import it.unimi.dsi.fastutil.objects.Reference2IntLinkedOpenHashMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.ForgeRegistries;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.statistics.history.EnchantmentHistory;
import soulboundarmory.serial.Serializable;
import soulboundarmory.util.Util2;

public class EnchantmentStorage extends Reference2IntLinkedOpenHashMap<Enchantment> implements Iterable<Enchantment>, Serializable {
	public final EnchantmentHistory history;

	protected final ItemComponent<?> component;

	public EnchantmentStorage(ItemComponent<?> component) {
		this.component = component;
		this.history = new EnchantmentHistory(component);
	}

	public void initialize(Predicate<Enchantment> predicate) {
		ForgeRegistries.ENCHANTMENTS.getValues().stream()
			.filter(enchantment -> enchantment.type.isAcceptableItem(this.component.item())
				&& !enchantment.isCursed()
				&& !Util2.contains(enchantment, Enchantments.UNBREAKING, Enchantments.MENDING))
			.filter(predicate)
			.forEach(enchantment -> this.put(enchantment, 0));
	}

	@Override
	public Integer get(Object enchantment) {
		// Can't use getOrDefault here due to recursion.
		var level = super.get(enchantment);
		return level == null ? 0 : level;
	}

	public void add(Enchantment enchantment, int levels) {
		this.put(enchantment, this.get(enchantment) + levels);
		this.history.record(enchantment, levels);
	}

	public void reset() {
		for (var enchantment : this) {
			this.put(enchantment, 0);
		}
	}

	@Override
	public void serialize(NbtCompound tag) {
		for (var enchantment : this) {
			var level = this.get(enchantment);

			if (level != null) {
				var identifier = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);

				if (identifier != null) {
					tag.putInt(identifier.toString(), level);
				}
			}
		}
	}

	@Override
	public void deserialize(NbtCompound tag) {
		for (var key : tag.getKeys()) {
			var enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new Identifier(key));

			if (this.containsKey(enchantment)) {
				this.put(enchantment, tag.getInt(key));
			}
		}
	}

	@Override
	public Iterator<Enchantment> iterator() {
		return this.keySet().iterator();
	}
}
