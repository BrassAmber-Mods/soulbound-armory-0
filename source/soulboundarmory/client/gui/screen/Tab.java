package soulboundarmory.client.gui.screen;

import cell.client.gui.widget.Widget;

public abstract class Tab<T extends Tab<T>> extends Widget<T> {
    public int index;

    protected Widget<?> button;

    public int top(int rows) {
        return this.top(this.height() / 16, rows);
    }

    public int top(int separation, int rows) {
        return (this.height() - (rows - 1) * separation) / 2;
    }

    public int height(int rows, int row) {
        return this.top(rows) + row * this.height() / 16;
    }

    public int height(int separation, int rows, int row) {
        return this.top(rows, separation) + row * separation;
    }
}
