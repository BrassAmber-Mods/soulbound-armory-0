package soulboundarmory.client.gui.screen;

import cell.client.gui.screen.CellScreen;
import cell.client.gui.widget.Widget;
import net.minecraft.text.Text;

public abstract class ScreenTab extends CellScreen {
    public int index;

    protected Widget<?> button;

    public ScreenTab(Text title) {
        super(title);
    }

    public Text label() {
        return this.title;
    }

    public int getTop(int rows) {
        return this.getTop(this.height / 16, rows);
    }

    public int getTop(int sep, int rows) {
        return (this.height - (rows - 1) * sep) / 2;
    }

    public int height(int rows, int row) {
        return this.getTop(rows) + row * this.height / 16;
    }

    public int height(int sep, int rows, int row) {
        return this.getTop(rows, sep) + row * sep;
    }
}