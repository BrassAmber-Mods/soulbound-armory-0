package soulboundarmory.text.format;

/**
 * A shorter version of {@link ColorFunction} that supplies colors without the previous color.
 */
@FunctionalInterface
public interface ColorSupplier extends ColorFunction {
    @Override
    default int apply(int previous) {
        return this.get();
    }

    /**
     * @return the new color of the {@link Style}.
     */
    int get();
}
