package soulboundarmory.client.gui.screen;

import cell.client.gui.widget.scalable.ScalableWidget;

public class SkillWidget extends ScalableWidget<SkillWidget> {
    @Override
    public void renderWidget() {
        this.withZ(500, super::renderWidget);
    }
}
