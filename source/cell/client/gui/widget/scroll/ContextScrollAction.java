package cell.client.gui.widget.scroll;

import cell.client.gui.widget.Widget;

@FunctionalInterface
public interface ContextScrollAction<T extends Widget<T>> {
    void scroll(T widget, double amount);
}
