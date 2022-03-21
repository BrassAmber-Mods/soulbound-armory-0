package soulboundarmory.lib.gui.widget;

import java.util.function.Consumer;
import soulboundarmory.lib.gui.coordinate.Offset;

public class TooltipWidget extends Widget<TooltipWidget> {
    @Override public <C extends Widget> C add(int index, C child) {
        return (C) super.add(index, child).offset(Offset.Type.ABSOLUTE);
    }

    @Override public TooltipWidget centeredText(Consumer<? super TextWidget> configure) {
        return this.text(configure);
    }

    @Override protected void render() {
        var mouseFocused = this.parent.filter(parent -> parent.mouseFocused).isPresent();
        this.withZ(() -> renderTooltipFromComponents(this.matrixes, this.listChildren(), mouseFocused ? mouseX() : this.x() - 8, mouseFocused ? mouseY() : this.y()));
    }
}
