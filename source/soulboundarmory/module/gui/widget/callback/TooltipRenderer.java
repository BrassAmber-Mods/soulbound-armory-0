package soulboundarmory.module.gui.widget.callback;

import net.minecraft.client.util.math.MatrixStack;
import soulboundarmory.module.gui.widget.Widget;

public interface TooltipRenderer<T extends Widget<T>> {
	void render(T widget, MatrixStack matrixes, double x, double y);
}
