package cell.client.gui.widget.callback;

import cell.client.gui.CellElement;
import cell.client.gui.widget.Widget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public interface TextProvider<T extends Widget<T>> extends TooltipRenderer<T> {
    Text get(T widget, int mouseX, int mouseY);

    @Override
    default void render(T widget, MatrixStack matrices, int mouseX, int mouseY) {
        CellElement.minecraft.currentScreen.renderTooltip(matrices, this.get(widget, mouseX, mouseY), mouseX, mouseY);
    }
}
