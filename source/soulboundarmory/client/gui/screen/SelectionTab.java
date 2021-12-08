package soulboundarmory.client.gui.screen;

import cell.client.gui.widget.callback.PressCallback;
import cell.client.gui.widget.scalable.ScalableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.network.server.C2SSelectItem;
import soulboundarmory.util.ItemUtil;

/**
 The item selection tab, which adds a button for each {@linkplain ItemComponent#isUnlocked unlocked} or {@linkplain ItemComponent#canConsume unlockable} item.
 When a button is pressed, {@linkplain C2SSelectItem a packet} is sent to the server and the held item is replaced by the selected item.
 */
public class SelectionTab extends SoulboundTab {
    public SelectionTab(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        var buttonWidth = 128;
        var buttonHeight = 20;
        var centerX = (this.width - buttonWidth) / 2;
        var separation = 32;

        var selection = this.parent.component.storages.values().stream().filter(storage -> storage.isUnlocked() || storage.canConsume(this.parent.stack)).toList();
        var top = (this.height - buttonHeight - separation * (selection.size() - 1)) / 2;

        for (int row = 0, size = selection.size(); row < size; row++) {
            var storage = selection.get(row);
            var button = this.add(new ScalableWidget().button()
                .x(centerX)
                .y(top + row * separation)
                .width(buttonWidth)
                .height(buttonHeight)
                .text(storage.name())
                .primaryAction(this.selectAction(storage))
            );

            if (this.parent.displayTabs()) {
                button.active = ItemUtil.inventory(this.parent.player).noneMatch(storage::accepts);
            }
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.render(matrices, mouseX, mouseY, partialTicks);

        if (this.parent.displayTabs() && !this.parent.storage.isItemEquipped()) {
            drawCenteredText(matrices, this.textRenderer, this.label(), this.width / 2, 40, 0xFFFFFF);
        }
    }

    protected PressCallback<ScalableWidget> selectAction(ItemComponent<?> storage) {
        return button -> Packets.serverSelectItem.send(new ExtendedPacketBuffer(storage).writeInt(this.parent.slot));
    }
}
