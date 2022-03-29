package soulboundarmory.module.config.gui;

import soulboundarmory.module.config.Property;
import soulboundarmory.module.gui.widget.Widget;

public class PropertyValueWidget<P, T extends PropertyValueWidget<P, T>> extends Widget<T> {
    protected final Property<P> property;

    public PropertyValueWidget(Property<P> property) {
        this.property = property;
    }
}
