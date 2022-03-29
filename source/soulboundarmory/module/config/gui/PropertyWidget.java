package soulboundarmory.module.config.gui;

import net.auoeke.reflect.Types;
import soulboundarmory.module.config.Property;
import soulboundarmory.module.gui.widget.Widget;

public class PropertyWidget extends Widget<PropertyWidget> {
    private final Property<?> property;
    private final Widget<?> value;

    public PropertyWidget(Property<?> property) {
        this.property = property;
        this.height(32);

        if (Types.equals(property.type, int.class)) {
            this.value = new IntegerPropertyWidget((Property<Integer>) property).alignRight().centerY().x(1, -8).y(.5).height(20);
        } else if (Types.equals(property.type, boolean.class)) {
            this.value = new BooleanPropertyWidget((Property<Boolean>) property).alignRight().centerY().x(1, -8).y(.5).width(50).height(20);
        } else if (property.type.isEnum()) {
            this.value = new EnumPropertyWidget((Property<Enum<?>>) property).alignRight().centerY().x(1, -8).y(.5).width(p -> Math.max(100, p.descendantWidth() + 10)).height(20);
        } else {
            this.value = null;
        }

        this.text(text -> text.text(this.property.name).centerY().x(8).y(.5));

        if (this.value != null) {
            this.add(this.value);
        }
    }

    @Override protected void render() {
        if (this.isHovered()) {
            fill(this.matrixes, this.absoluteX(), this.absoluteY(), this.absoluteEndX(), this.absoluteEndY(), this.z(), 0x20FFFFFF);
        }
    }
}
