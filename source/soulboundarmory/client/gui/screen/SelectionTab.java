package soulboundarmory.client.gui.screen;

import soulboundarmory.client.gui.widget.SelectionEntryWidget;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.config.Configuration;
import soulboundarmory.module.gui.widget.WidgetBox;
import soulboundarmory.util.ItemUtil;

/**
 The item selection tab, which adds a button for each {@linkplain ItemComponent#canConsume unlockable} item in the inventory and each {@linkplain ItemComponent#isUnlocked unlocked} item.
 When a button is pressed, {@link ItemComponent#select} is invoked.
 */
public class SelectionTab extends Tab {
	public SelectionTab() {
		super(Translations.guiToolSelection);
	}

	@Override public void initialize() {
		var parent = this.container();
		var component = parent.component;
		var box = this.add(new WidgetBox<>().center());

		if (Configuration.Client.selectionEntryType == SelectionEntryWidget.Type.ICON) {
			box.xSpacing(80).x(0.5).y(1D);
		} else {
			box.ySpacing(24).x(1D).y(0.5D);
		}

		component.items.values().stream()
			.filter(ItemComponent::isEnabled)
			.filter(item -> item.isUnlocked() && component.accepts(parent.stack) || item.canConsume(parent.stack))
			.forEach(tool -> box.add(new SelectionEntryWidget(tool))
				.center()
				.primaryAction(() -> tool.select(parent.slot))
				.active(() -> (tool.canConsume(parent.stack) || component.cooledDown()) && (!parent.displayTabs() || ItemUtil.inventory(player()).noneMatch(tool::accepts)))
			);
	}
}
