package soulboundarmory.module.config.gui;

import soulboundarmory.module.config.Property;
import soulboundarmory.module.gui.widget.ScalableWidget;

public class BooleanPropertyWidget extends ScalableWidget<BooleanPropertyWidget> {
	protected final Property<Boolean> property;

	public BooleanPropertyWidget(Property<Boolean> property) {
		this.property = property;
		this.button().centeredText(text -> text.text(this.property::get)).primaryAction(() -> this.property.set(!this.property.get()));
	}
}
