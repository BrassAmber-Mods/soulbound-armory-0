package user11681.soulboundarmory.client.gui.screen;

import net.minecraft.client.util.math.MatrixStack;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.MinecraftClient;
import user11681.cell.client.gui.widget.callback.PressCallback;
import user11681.cell.client.gui.widget.scalable.ScalableWidget;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.registry.Packets;

public class SelectionTab extends SoulboundTab {
    public SelectionTab() {
        super(Translations.menuWeaponSelection);
    }

    @Override
    public void init(MinecraftClient client, int width, int height) {
        super.init(client, width, height);

        int buttonWidth = 128;
        int buttonHeight = 20;
        int centerX = (this.width - buttonWidth) / 2;
        int ySep = 32;

        List<ItemStorage<?>> selection = this.parent.capability.storages().values().stream().filter(storage -> storage.isUnlocked() || storage.canUnlock()).collect(Collectors.toList());
        int top = (this.height - buttonHeight - ySep * (selection.size() - 1)) / 2;

        for (int row = 0, size = selection.size(); row < size; row++) {
            ItemStorage<?> storage = selection.get(row);
            ScalableWidget button = this.add(
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
            drawCenteredText(matrices, this.textRenderer, this.label(), this.width / 2, 40, 0xFFFFFF);
        }
    }

    protected PressCallback<ScalableWidget> selectAction(ItemStorage<?> storage) {
        return button -> {
            Packets.serverItemType.send(new ExtendedPacketBuffer(storage).writeInt(this.parent.slot));
            this.onClose();
        };
    }
}
