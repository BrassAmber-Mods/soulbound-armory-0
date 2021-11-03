package net.auoeke.cell.client.gui.widget.callback;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.auoeke.cell.client.gui.widget.Widget;

@OnlyIn(Dist.CLIENT)
public interface PressCallback<T extends Widget<T>> {
    void onPress(T widget);
}
