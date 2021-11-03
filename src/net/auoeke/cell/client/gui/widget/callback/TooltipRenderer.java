package net.auoeke.cell.client.gui.widget.callback;

import net.minecraft.client.util.math.MatrixStack;
import net.auoeke.cell.client.gui.widget.Widget;

public interface TooltipRenderer<T extends Widget<T>> {
    void render(T widget, MatrixStack matrices, int mouseX, int mouseY);
}
