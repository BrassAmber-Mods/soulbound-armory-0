package soulboundarmory.module.gui.widget.callback;

import soulboundarmory.module.gui.widget.Widget;

public interface PressCallback<T extends Widget<T>> {
	static <T extends Widget<T>> PressCallback<T> of(Runnable runnable) {
		return widget -> runnable.run();
	}

	void onPress(T widget);
}
