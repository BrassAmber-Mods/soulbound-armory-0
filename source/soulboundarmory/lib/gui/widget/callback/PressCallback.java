package soulboundarmory.lib.gui.widget.callback;

import soulboundarmory.lib.gui.widget.Widget;

public interface PressCallback<T extends Widget<T>> {
    static <T extends Widget<T>> PressCallback<T> of(Runnable runnable) {
        return widget -> runnable.run();
    }

    void onPress(T widget);
}
