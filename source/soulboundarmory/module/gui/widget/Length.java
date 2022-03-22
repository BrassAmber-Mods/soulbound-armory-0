package soulboundarmory.module.gui.widget;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

public class Length {
    private DoubleSupplier value = () -> 1F;
    private boolean absolute;

    public int get() {
        return (int) this.value.getAsDouble();
    }

    public int get(int max) {
        var value = this.value.getAsDouble();
        return (int) (this.absolute ? value : value * max);
    }

    public Length set(double value) {
        this.value = () -> value;

        return this.absolute(false);
    }

    public Length set(int value) {
        this.value = () -> value;

        return this.absolute(true);
    }

    public Length set(DoubleSupplier value) {
        this.value = value;

        return this.absolute(false);
    }

    public Length set(IntSupplier value) {
        this.value = value::getAsInt;

        return this.absolute(true);
    }

    public Length absolute(boolean absolute) {
        this.absolute = absolute;

        return this;
    }
}
