package soulboundarmory.client.gui.screen;

import cell.client.gui.widget.callback.PressCallback;
import cell.client.gui.widget.scalable.ScalableWidget;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.stream.Collectors;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;

public class SelectionTab extends SoulboundTab {
    public SelectionTab() {
        super(Translations.menuWeaponSelection);
    }

    @Override
    protected void init() {
        var buttonWidth = 128;
        var buttonHeight = 20;
        var centerX = (this.width - buttonWidth) / 2;
        var ySep = 32;

        var selection = this.parent.component.storages().values().stream().filter(storage -> storage.isUnlocked() || storage.canUnlock()).collect(Collectors.toList());
        var top = (this.height - buttonHeight - ySep * (selection.size() - 1)) / 2;

        for (int row = 0, size = selection.size(); row < size; row++) {
            var storage = selection.get(row);
            var button = this.add(new ScalableWidget().button()
                .x(centerX)
                .y(top + (row * ySep))
                .width(buttonWidth)
                .height(buttonHeight)
                .text(storage.getName())
                .primaryAction(this.selectAction(storage))
            );

            if (this.parent.displayTabs()) {
                button.active = storage.type() != this.parent.storage.type();
            }
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.render(matrices, mouseX, mouseY, partialTicks);

        if (!this.parent.storage.itemEquipped()) {
            drawCenteredString(matrices, this.textRenderer, this.label(), this.width / 2, 40, 0xFFFFFF);
        }
    }

    protected PressCallback<ScalableWidget> selectAction(ItemStorage<?> storage) {
        return button -> {
            Packets.serverItemType.send(new ExtendedPacketBuffer(storage).writeInt(this.parent.slot));
            this.onClose();
        };
    }
}
