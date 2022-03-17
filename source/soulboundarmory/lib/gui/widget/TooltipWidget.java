package soulboundarmory.lib.gui.widget;

import soulboundarmory.lib.gui.coordinate.Offset;

public class TooltipWidget extends Widget<TooltipWidget> {
    @Override public <C extends Widget> C add(int index, C child) {
        return (C) super.add(index, child).offset(Offset.Type.ABSOLUTE);
    }

    @Override protected void render() {
        this.withZ(() -> renderTooltipFromComponents(this.matrixes, this.listChildren(), this.x(), this.y()));
    }
}
