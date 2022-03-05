package soulboundarmory.client.gui.screen;

import soulboundarmory.lib.gui.widget.scalable.ScalableWidget;

public class SkillWidget extends ScalableWidget<SkillWidget> {
    @Override
    public void render() {
        this.withZ(500, super::render);
    }
}
