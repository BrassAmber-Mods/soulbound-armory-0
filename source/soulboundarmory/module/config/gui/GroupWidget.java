package soulboundarmory.module.config.gui;

import soulboundarmory.module.config.Group;
import soulboundarmory.module.gui.widget.TextWidget;
import soulboundarmory.module.gui.widget.WidgetBox;

public class GroupWidget extends WidgetBox<GroupWidget> {
    private boolean expanded;

    public GroupWidget(Group group) {
        this.ySpacing(8).add(new EntryWidget<>(group.comment).height(32).with(new TextWidget().text("> " + group.name).centerY().x(8).y(.5)).primaryAction(() -> this.expanded ^= true));
        this.add(new CategoryWidget(group.children().toList()).x(8).y(32).with(c -> c.present(() -> this.expanded)));
    }
}
