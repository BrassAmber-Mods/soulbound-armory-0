package soulboundarmory.module.config.gui;

import soulboundarmory.module.gui.widget.TooltipWidget;
import soulboundarmory.module.gui.widget.Widget;

public class EntryWidget<T extends EntryWidget<T>> extends Widget<T> {
	public EntryWidget(String comment) {
		this.height(32);

		if (comment != null) {
			this.tooltip(new TooltipWidget().text(comment));
		}
	}

	@Override protected void render() {
		if (this.isFocused() || this.isHovered()) {
			fill(this.matrixes, this.absoluteX(), this.absoluteY(), this.absoluteEndX(), this.absoluteEndY(), this.z(), 0x20FFFFFF);
		}
	}
}
