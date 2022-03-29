package soulboundarmory.module.config.gui;

import soulboundarmory.module.config.Node;
import soulboundarmory.module.config.Property;
import soulboundarmory.module.gui.widget.Widget;
import soulboundarmory.module.gui.widget.WidgetBox;

public class CategoryWidget extends Widget<CategoryWidget> {
    private final WidgetBox properties = this.add(new WidgetBox().vertical());

    public CategoryWidget(Iterable<Node> nodes) {
        for (var node : nodes) {
            if (node instanceof Property<?> property) {
                this.properties.add(new PropertyWidget(property));
            }
        }
    }
}
