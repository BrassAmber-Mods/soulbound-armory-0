package soulboundarmory.lib.gui.widget;

public class TooltipWidget extends Widget<TooltipWidget> {
    @Override
    protected void render() {
        renderTooltipFromComponents(this.matrixes, this.listChildren(), this.x(), this.y());
    }
}
