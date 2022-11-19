package soulboundarmory.module.gui.widget.callback;

import java.util.List;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import soulboundarmory.module.gui.widget.Widget;

public interface TooltipProvider<T extends Widget<T>> extends TooltipRenderer<T> {
	List<? extends StringVisitable> get();

	@Override
	default void render(T widget, MatrixStack matrixes, double x, double y) {
		widget.renderTooltip(x, y, this.get());
	}
}
