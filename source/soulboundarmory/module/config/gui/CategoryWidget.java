package soulboundarmory.module.config.gui;

import java.util.List;
import net.auoeke.reflect.Types;
import soulboundarmory.module.config.Node;
import soulboundarmory.module.config.Property;
import soulboundarmory.module.gui.widget.Widget;
import soulboundarmory.module.gui.widget.WidgetBox;

public class CategoryWidget extends Widget<CategoryWidget> {
    private final String name;
    private final List<Node> nodes;
    private final WidgetBox properties = this.add(new WidgetBox().vertical());

    public CategoryWidget(String name, List<Node> nodes) {
        this.name = name;
        this.nodes = nodes;

        for (var node : nodes) {
            if (node instanceof Property<?> property) {
                this.properties.add(new PropertyWidget(property));
            }
        }
    }
}
