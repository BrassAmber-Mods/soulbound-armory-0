package soulboundarmory.client.gui.screen;

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
    public void initialize() {
        var buttonWidth = 128;
        var buttonHeight = 20;
        var centerX = (this.width() - buttonWidth) / 2;
        var separation = 32;
        var selection = this.parent().component.items.values().stream().filter(storage -> storage.isUnlocked() || storage.canConsume(this.parent().stack)).toList();
        var size = selection.size();
        var top = (this.height() - buttonHeight - separation * (size - 1)) / 2;

        for (var row = 0; row < size; row++) {
            var storage = selection.get(row);
            this.add(new ScalableWidget<>().button()
                .x(centerX)
                .y(top + row * separation)
                .width(buttonWidth)
                .height(buttonHeight)
                .text(storage.name())
                .primaryAction(widget -> Packets.serverSelectItem.send(new ExtendedPacketBuffer(storage).writeInt(this.parent().slot)))
                .active(!this.parent().displayTabs() || ItemUtil.inventory(this.parent().player).noneMatch(storage::accepts))
            );
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.render(matrices, mouseX, mouseY, partialTicks);

        if (this.parent().displayTabs() && !this.parent().item.isItemEquipped()) {
            drawCenteredText(matrices, textDrawer, this.title, this.width() / 2, 40, 0xFFFFFF);
        }
    }

}
