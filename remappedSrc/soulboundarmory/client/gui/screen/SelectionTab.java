package soulboundarmory.client.gui.screen;

import I;
import cell.client.gui.widget.callback.PressCallback;
import cell.client.gui.widget.scalable.ScalableWidget;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.util.math.MatrixStack;
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
        I buttonWidth = 128;
        I buttonHeight = 20;
        I centerX = (this.width - buttonWidth) / 2;
        I ySep = 32;

        List selection = this.parent.component.storages().values().stream().filter(storage -> storage.isUnlocked() || storage.canUnlock()).collect(Collectors.toList());
        I top = (this.height - buttonHeight - ySep * (selection.size() - 1)) / 2;

        for (int row = 0, size = selection.size(); row < size; row++) {
            ItemStorage storage = selection.get(row);
            ScalableWidget button = this.add(new ScalableWidget().button()
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
            drawCenteredText(matrices, this.textRenderer, this.label(), this.width / 2, 40, 0xFFFFFF);
        }
    }

    protected PressCallback<ScalableWidget> selectAction(ItemStorage<?> storage) {
        return button -> {
            Packets.serverItemType.send(new ExtendedPacketBuffer(storage).writeInt(this.parent.slot));
            this.removed();
        };
    }
}
