package soulboundarmory.module.config.gui;

import java.math.BigDecimal;
import soulboundarmory.module.config.Property;

public class IntegerPropertyWidget extends TextPropertyWidget<Integer> {
	public IntegerPropertyWidget(Property<Integer> property) {
		super(property);
	}

	@Override protected void validate() {
		this.property.set(new BigDecimal(this.text()).intValueExact());
	}
}
