package soulboundarmory.client.gui.screen;

import soulboundarmory.lib.gui.widget.scalable.ScalableWidget;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.util.ItemUtil;
import soulboundarmory.util.Util;

/**
 The item selection tab, which adds a button for each {@linkplain ItemComponent#canConsume unlockable} item in the inventory and each {@linkplain ItemComponent#isUnlocked unlocked} item.
 When a button is pressed, {@link ItemComponent#select} is invoked.
 */
public class SelectionTab extends SoulboundTab {
    public SelectionTab() {
        super(Translations.guiToolSelection);
    }

    @Override
    public void initialize() {
        var parent = this.container();
        var selection = parent.component.items.values().stream().filter(item -> item.isUnlocked() && parent.component.accepts(parent.stack) || item.canConsume(parent.stack)).toList();

        Util.enumerate(selection, (component, row) -> this.add(new ScalableWidget<>()
            .button()
            .x(.5)
            .y(this.height(24, selection.size(), row))
            .center()
            .size(128, 20)
            .text(component.name())
            .primaryAction(() -> component.select(parent.slot))
            .active(() -> !parent.displayTabs() || ItemUtil.inventory(player()).noneMatch(component::accepts))
        ));
    }
}
