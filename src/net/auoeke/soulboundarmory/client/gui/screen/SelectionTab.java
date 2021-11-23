package net.auoeke.soulboundarmory.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.stream.Collectors;
import net.auoeke.cell.client.gui.widget.callback.PressCallback;
import net.auoeke.cell.client.gui.widget.scalable.ScalableWidget;
import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.auoeke.soulboundarmory.client.i18n.Translations;
import net.auoeke.soulboundarmory.network.ExtendedPacketBuffer;
import net.auoeke.soulboundarmory.registry.Packets;
import net.minecraft.client.Minecraft;

public class SelectionTab extends SoulboundTab {
    public SelectionTab() {
        super(Translations.menuWeaponSelection);
    }

    @Override
    public void init(Minecraft client, int width, int height) {
        super.init(client, width, height);

        var buttonWidth = 128;
        var buttonHeight = 20;
        var centerX = (this.width - buttonWidth) / 2;
        var ySep = 32;

        var selection = this.parent.capability.storages().values().stream().filter(storage -> storage.isUnlocked() || storage.canUnlock()).collect(Collectors.toList());
        var top = (this.height - buttonHeight - ySep * (selection.size() - 1)) / 2;

        for (int row = 0, size = selection.size(); row < size; row++) {
            var storage = selection.get(row);
            var button = this.add(
                new ScalableWidget().button()
                    .x(centerX)
                    .y(top + (row * ySep))
                    .width(buttonWidth)
                    .height(buttonHeight)
                    .text(storage.getName())
                    .primaryAction(this.selectAction(storage))
            );

            if (this.parent.displayTabs()) {
                button.active = storage.type().id() != this.parent.storage.type().id();
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
