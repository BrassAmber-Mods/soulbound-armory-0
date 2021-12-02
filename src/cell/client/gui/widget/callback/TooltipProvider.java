package cell.client.gui.widget.callback;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import cell.client.gui.CellElement;
import cell.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

public interface TooltipProvider<T extends Widget<T>> extends TooltipRenderer<T> {
    List<ITextComponent> get(T widget, int mouseX, int mouseY);

    @Override
    default void render(T widget, MatrixStack matrices, int mouseX, int mouseY) {
        CellElement.client.currentScreen.func_243308_b(matrices, this.get(widget, mouseX, mouseY), mouseX, mouseY);
    }
}
