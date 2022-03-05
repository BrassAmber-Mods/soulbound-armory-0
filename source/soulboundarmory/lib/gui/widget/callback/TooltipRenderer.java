package soulboundarmory.lib.gui.widget.callback;

import soulboundarmory.lib.gui.widget.Widget;
import net.minecraft.client.util.math.MatrixStack;

public interface TooltipRenderer<T extends Widget<T>> {
    void render(T widget, MatrixStack matrixes, double x, double y);
}
