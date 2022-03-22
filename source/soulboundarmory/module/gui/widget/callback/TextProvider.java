package soulboundarmory.module.gui.widget.callback;

import soulboundarmory.module.gui.widget.Widget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;

public interface TextProvider<T extends Widget<T>> extends TooltipRenderer<T> {
    StringVisitable get();

    @Override
    default void render(T widget, MatrixStack matrixes, double x, double y) {
        widget.renderTooltip(x, y, this.get());
    }
}
