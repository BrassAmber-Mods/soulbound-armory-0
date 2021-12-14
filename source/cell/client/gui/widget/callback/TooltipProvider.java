package cell.client.gui.widget.callback;

import cell.client.gui.widget.Widget;
import java.util.List;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public interface TooltipProvider<T extends Widget<T>> extends TooltipRenderer<T> {
    List<Text> get(T widget, double mouseX, double mouseY);

    @Override
    default void render(T widget, MatrixStack matrices, double x, double y) {
        widget.renderTooltip(this.get(widget, x, y));
    }
}
