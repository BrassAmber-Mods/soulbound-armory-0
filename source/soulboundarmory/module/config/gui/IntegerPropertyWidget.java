package soulboundarmory.module.config.gui;

import soulboundarmory.module.config.Property;
import soulboundarmory.module.gui.widget.TextBoxWidget;

public class IntegerPropertyWidget extends TextBoxWidget {
    public IntegerPropertyWidget(Property<Integer> property) {
        this.line().append(property.get());
    }
}
