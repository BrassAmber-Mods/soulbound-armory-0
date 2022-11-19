package soulboundarmory.module.config.gui;

import java.math.BigDecimal;
import soulboundarmory.module.config.Property;

public class DoublePropertyWidget extends TextPropertyWidget<Double> {
	public DoublePropertyWidget(Property<Double> property) {
		super(property);
	}

	@Override protected void validate() {
		this.property.set(new BigDecimal(this.text()).doubleValue());
	}
}
