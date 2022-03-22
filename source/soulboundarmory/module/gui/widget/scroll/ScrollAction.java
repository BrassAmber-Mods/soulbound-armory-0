package soulboundarmory.module.gui.widget.scroll;

import soulboundarmory.module.gui.widget.Widget;

@FunctionalInterface
public interface ScrollAction<T extends Widget<T>> extends ContextScrollAction<T> {
    void scroll(double amount);

    @Override
    default void scroll(T widget, double amount) {
        this.scroll(amount);
    }
}
