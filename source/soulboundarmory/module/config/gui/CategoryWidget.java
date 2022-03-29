package soulboundarmory.module.config.gui;

import soulboundarmory.module.config.Group;
import soulboundarmory.module.config.Node;
import soulboundarmory.module.config.Property;
import soulboundarmory.module.gui.widget.WidgetBox;

public class CategoryWidget extends WidgetBox<CategoryWidget> {
    public CategoryWidget(Iterable<Node> nodes) {
        this.vertical().width(category -> category.parent.get().absoluteEndX() - category.absoluteX());

        for (var node : nodes) {
            if (node instanceof Property<?> property) {
                this.add(new PropertyWidget(property));
            } else if (node instanceof Group group) {
                this.add(new GroupWidget(group));
            }
        }
    }
}
