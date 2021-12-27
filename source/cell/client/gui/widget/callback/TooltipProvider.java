package cell.client.gui.widget.callback;

import cell.client.gui.widget.Widget;
import java.util.List;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;

public interface TooltipProvider<T extends Widget<T>> extends TooltipRenderer<T> {
    List<? extends StringVisitable> get();

    @Override
    default void render(T widget, MatrixStack matrixes, double x, double y) {
        widget.renderTooltip(x, y, this.get());
    }
}
