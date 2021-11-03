package net.auoeke.cell.client.gui.widget;

public class Length {
    public float value = 1F;
    public boolean absolute;

    public int get() {
        return (int) this.value;
    }

    public int get(int max) {
        return (int) (this.absolute ? this.value : this.value * max);
    }

    public Length set(float value) {
        this.value = value;

        return this.absolute(false);
    }

    public Length set(int value) {
        this.value = value;

        return this.absolute(true);
    }

    public Length absolute(boolean absolute) {
        this.absolute = absolute;

        return this;
    }
}
