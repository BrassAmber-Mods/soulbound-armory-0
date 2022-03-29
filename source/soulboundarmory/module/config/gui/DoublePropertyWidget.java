package soulboundarmory.module.config.gui;

import soulboundarmory.module.config.Property;
import soulboundarmory.module.gui.widget.TextBoxWidget;

public class DoublePropertyWidget extends TextBoxWidget {
    public DoublePropertyWidget(Property<Double> property) {
        this.line().append(property.get());
    }

    @Override public boolean charTyped(char character, int modifiers) {
        if (super.charTyped(character, modifiers)) {
        }

        return false;
    }
}
