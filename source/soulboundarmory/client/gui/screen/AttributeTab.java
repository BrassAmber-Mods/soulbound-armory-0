package soulboundarmory.client.gui.screen;

import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.Statistic;
import soulboundarmory.module.gui.widget.Widget;
import soulboundarmory.module.gui.widget.WidgetBox;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;

public class AttributeTab extends Tab {
	public AttributeTab() {
		super(Translations.guiButtonAttributes);
	}

	@Override public void initialize() {
		var component = this.container().item();
		var attributeTypes = component.screenAttributes();
		var attributes = attributeTypes.stream().map(component::statistic).toList();
		var length = Math.max(this.container().xpBar.width(), width(attributeTypes.stream().map(component::format)) + 60);
		this.add(this.resetButton(Category.attribute)).active(() -> component.statistics.get(Category.attribute).values().stream().anyMatch(Statistic::aboveMin));
		this.displayPoints(component::attributePoints);

		var box = this.add(new WidgetBox<>().ySpacing(4).center().x(.5).y(.5).width(length));
		attributes.forEach(statistic -> box.add(new Widget<>().width(length).height(20).with(row -> {
			row.add(this.squareButton("-", () -> this.removePoint(statistic)).alignRight().x(1, -20).active(statistic::aboveMin));
			row.add(this.squareButton("+", () -> this.addPointAction(statistic)).alignRight().x(1D).active(() -> component.attributePoints() > 0 && statistic.belowMax()));
			row.text(widget -> widget.shadow().text(() -> component.format(statistic.type)));
		})));
	}

	protected void addPointAction(Statistic statistic) {
		Packets.serverAttribute.send(new ExtendedPacketBuffer(this.container().item())
			.writeIdentifier(statistic.type.id())
			.writeInt(isShiftDown() ? Integer.MAX_VALUE : 1)
		);
	}

	protected void removePoint(Statistic statistic) {
		Packets.serverAttribute.send(new ExtendedPacketBuffer(this.container().item())
			.writeIdentifier(statistic.type.id())
			.writeInt(isShiftDown() ? Integer.MIN_VALUE : -1)
		);
	}
}
