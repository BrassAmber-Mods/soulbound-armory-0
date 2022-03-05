package soulboundarmory.lib.gui.widget.scroll;

import soulboundarmory.lib.gui.widget.Widget;

@FunctionalInterface
public interface ContextScrollAction<T extends Widget<T>> {
    void scroll(T widget, double amount);
}
