package soulboundarmory.module.config.gui;

import java.util.Arrays;
import soulboundarmory.module.config.Property;
import soulboundarmory.module.gui.widget.ScalableWidget;

public class EnumPropertyWidget extends ScalableWidget<EnumPropertyWidget> {
	public EnumPropertyWidget(Property<Enum<?>> property) {
		var values = Arrays.asList(property.type.getEnumConstants());
		this.button().centeredText(text -> text.text(property::get)).primaryAction(() -> property.set(values.get((values.indexOf(property.get()) + 1) % values.size())));
	}
}
