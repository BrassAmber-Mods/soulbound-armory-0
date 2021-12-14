package cell.client.gui.widget.callback;

import cell.client.gui.widget.Widget;

public interface PressCallback<T extends Widget<T>> {
    static <T extends Widget<T>> PressCallback<T> of(Runnable runnable) {
        return widget -> runnable.run();
    }

    void onPress(T widget);
}
