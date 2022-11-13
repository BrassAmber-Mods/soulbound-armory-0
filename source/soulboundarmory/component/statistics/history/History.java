package soulboundarmory.component.statistics.history;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.serial.Serializable;
import soulboundarmory.util.Util2;

public abstract class History<T extends Record> implements Serializable {
	private final ReferenceArrayList<T> records = new ReferenceArrayList<>();

	protected final ItemComponent<?> component;

	public History(ItemComponent<?> component) {
		this.component = component;
	}

	public void revert(int level) {
		for (var history = this.records.iterator(); history.hasNext(); ) {
			var record = history.next();

			if (record.revert(level)) {
				break;
			}

			history.remove();
		}
	}

	public void pop() {
		if (!this.records.isEmpty()) {
			this.records.pop().pop();
		}
	}

	@Override
	public void deserialize(NbtCompound tag) {
		tag.getList("records", NbtElement.COMPOUND_TYPE).forEach(element -> {
			var record = this.skeleton();
			record.deserialize((NbtCompound) element);
			this.records.add(record);
		});
	}

	@Override
	public void serialize(NbtCompound tag) {
		tag.put("records", this.records.stream().map(Record::serialize).collect(NbtList::new, NbtList::add, Util2::nul)); // Throws if combiner is null.
	}

	protected abstract T skeleton();

	protected final void record(T record) {
		this.records.add(record);
	}
}
