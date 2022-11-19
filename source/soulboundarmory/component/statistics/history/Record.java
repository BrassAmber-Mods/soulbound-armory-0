package soulboundarmory.component.statistics.history;

import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.serial.Serializable;

public abstract class Record implements Serializable {
	protected final ItemComponent<?> component;
	protected int level;

	public Record(ItemComponent<?> component) {
		this.component = component;
	}

	/**
	 Restore the component's state to the minimum extent necessary in order to revert to a target level.

	 @param level the target level
	 @return whether the component has been fully restored to the required state
	 */
	public abstract boolean revert(int level);

	/**
	 Undo this record.
	 */
	public abstract void pop();
}
