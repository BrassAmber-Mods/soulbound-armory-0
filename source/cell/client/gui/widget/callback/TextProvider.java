package cell.client.gui.widget.callback;

import cell.client.gui.widget.Widget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public interface TextProvider<T extends Widget<T>> extends TooltipRenderer<T> {
    Text get(T widget, double mouseX, double mouseY);

    @Override
    default void render(T widget, MatrixStack matrices, double x, double y) {
        widget.renderTooltip(this.get(widget, x, y));
    }
}
