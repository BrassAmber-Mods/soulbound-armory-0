package cell.client.gui.widget.callback;

import cell.client.gui.widget.Widget;
import net.minecraft.client.util.math.MatrixStack;

public interface TooltipRenderer<T extends Widget<T>> {
    void render(T widget, MatrixStack matrices, double x, double y);
}
