package soulboundarmory.module.config.gui;

import java.util.List;
import soulboundarmory.module.config.Node;
import soulboundarmory.module.gui.widget.Widget;

public class CategoryWidget extends Widget<CategoryWidget> {
    private final String name;
    private final List<Node> nodes;

    public CategoryWidget(String name, List<Node> nodes) {
        this.name = name;
        this.nodes = nodes;
    }

    @Override public void initialize() {

    }
}
