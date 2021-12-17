package soulboundarmory.client.gui.screen;

import cell.client.gui.widget.scalable.ScalableWidget;
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
        var selection = this.parent().component.items.values().stream().filter(storage -> storage.isUnlocked() || storage.canConsume(this.parent().stack)).toList();

        Util.enumerate(selection, (component, row) -> this.add(new ScalableWidget<>()
            .button()
            .x(this.middleX())
            .y(this.height(32, selection.size(), row))
            .center()
            .width(128)
            .height(20)
            .text(component.name())
            .primaryAction(() -> component.select(this.parent().slot))
            .active(!this.parent().displayTabs() || ItemUtil.inventory(player()).noneMatch(component::accepts))
        ));
    }
}
