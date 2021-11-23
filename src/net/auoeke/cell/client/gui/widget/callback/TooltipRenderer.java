package net.auoeke.cell.client.gui.widget.callback;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.auoeke.cell.client.gui.widget.Widget;

public interface TooltipRenderer<T extends Widget<T>> {
    void render(T widget, MatrixStack matrices, int mouseX, int mouseY);
}
