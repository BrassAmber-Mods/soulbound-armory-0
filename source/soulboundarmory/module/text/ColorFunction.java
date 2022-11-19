package soulboundarmory.module.text;

/**
 A callback that produces a new color based on the previous color.
 */
@FunctionalInterface
public interface ColorFunction {
	/**
	 @param previous the previous color of the {@link Style}.
	 @return the new color of the {@link Style}
	 */
	int apply(int previous);
}
