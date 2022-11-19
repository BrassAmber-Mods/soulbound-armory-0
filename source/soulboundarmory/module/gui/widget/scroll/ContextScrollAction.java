package soulboundarmory.module.gui.widget.scroll;

import soulboundarmory.module.gui.widget.Widget;

@FunctionalInterface
public interface ContextScrollAction<T extends Widget<T>> {
	void scroll(T widget, double amount);
}
