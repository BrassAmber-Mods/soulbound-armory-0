package soulboundarmory.module.config.gui;

import java.util.Objects;
import soulboundarmory.module.config.Group;
import soulboundarmory.module.gui.widget.ScalableWidget;
import soulboundarmory.module.gui.widget.Widget;

public class GroupWidget extends Widget<GroupWidget>  {
    private CategoryWidget category;

    public GroupWidget(Group group) {
        var tab = this.add(new CategoryWidget(group.children.values()).y(8).with(c -> c.present(() -> c == this.category)));
        this.category = Objects.requireNonNullElse(this.category, tab);
    }
}
