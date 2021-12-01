package cell.client.gui.widget.callback;

import com.mojang.blaze3d.matrix.MatrixStack;
import cell.client.gui.CellElement;
import cell.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

public interface TextProvider<T extends Widget<T>> extends TooltipRenderer<T> {
    ITextComponent get(T widget, int mouseX, int mouseY);

    @Override
    default void render(T widget, MatrixStack matrices, int mouseX, int mouseY) {
        CellElement.client.screen.renderTooltip(matrices, this.get(widget, mouseX, mouseY), mouseX, mouseY);
    }
}
