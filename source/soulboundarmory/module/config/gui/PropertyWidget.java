package soulboundarmory.module.config.gui;

import net.auoeke.reflect.Types;
import soulboundarmory.module.config.Property;
import soulboundarmory.module.gui.widget.Widget;

public class PropertyWidget extends EntryWidget<PropertyWidget> {
    private final Property<?> property;
    private final Widget<?> value;

    public PropertyWidget(Property<?> property) {
        this.property = property;
        this.text(text -> text.text(this.property.name).centerY().x(8).y(.5));

        if (Types.equals(property.type, int.class)) {
            this.value = new IntegerPropertyWidget((Property<Integer>) property).width(100).height(11);
        } else if (Types.equals(property.type, double.class)) {
            this.value = new IntegerPropertyWidget((Property<Integer>) property).width(100).height(11);
        } else if (Types.equals(property.type, boolean.class)) {
            this.value = new BooleanPropertyWidget((Property<Boolean>) property).width(50).height(20);
        } else if (property.type.isEnum()) {
            this.value = new EnumPropertyWidget((Property<Enum<?>>) property).width(p -> Math.max(100, p.descendantWidth() + 10)).height(20);
        } else {
            this.value = null;
            return;
        }

        this.add(this.value.x(1, -8).y(.5).alignRight().centerY());
    }
}
