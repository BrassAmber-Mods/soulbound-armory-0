package cell.client.gui.widget.scroll;

import cell.client.gui.widget.Widget;

@FunctionalInterface
public interface ScrollAction<T extends Widget<T>> extends ContextScrollAction<T> {
    void scroll(double amount);

    @Override
    default void scroll(T widget, double amount) {
        this.scroll(amount);
    }
}
