package soulboundarmory.module.config.gui;

import soulboundarmory.module.gui.widget.Widget;

public class EntryWidget<T extends EntryWidget<T>> extends Widget<T> {
    public EntryWidget() {
        this.height(32);
    }

    @Override protected void render() {
        if (this.isFocused() || this.isHovered()) {
            fill(this.matrixes, this.absoluteX(), this.absoluteY(), this.absoluteEndX(), this.absoluteEndY(), this.z(), 0x20FFFFFF);
        }
    }
}
