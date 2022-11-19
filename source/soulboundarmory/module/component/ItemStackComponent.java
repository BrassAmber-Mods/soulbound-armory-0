package soulboundarmory.module.component;

import javax.annotation.Nonnull;

/**
 A component for an item stack.
 */
public interface ItemStackComponent<C extends ItemStackComponent<C>> extends Component<C> {
	/**
	 Finish initializing after the item stack is constructed.
	 */
	default void initialize() {}

	/**
	 Invoked at the end of every tick.
	 */
	default void tickEnd() {}

	/**
	 Implementations can override this method in order to affect equality between item stacks with the same component.
	 The default implementation compares their {@linkplain #serialize() tags}.
	 */
	default boolean equals(@Nonnull C other) {
		return this.serialize().equals(other.serialize());
	}
}
