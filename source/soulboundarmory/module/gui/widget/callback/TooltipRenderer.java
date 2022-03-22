package soulboundarmory.module.gui.widget.callback;

import soulboundarmory.module.gui.widget.Widget;
import net.minecraft.client.util.math.MatrixStack;

public interface TooltipRenderer<T extends Widget<T>> {
    void render(T widget, MatrixStack matrixes, double x, double y);
}
