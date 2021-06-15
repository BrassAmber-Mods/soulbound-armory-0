package user11681.soulboundarmory.client.gui.screen.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import user11681.cell.client.gui.widget.callback.PressCallback;
import user11681.cell.client.gui.widget.scalable.ScalableWidget;
import user11681.cell.client.gui.widget.scalable.ScalableWidgets;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.registry.Packets;

public class SelectionTab extends SoulboundTab {
    public SelectionTab(ITextComponent title, SoulboundCapability component, List<ScreenTab> tabs) {
        super(title, component, tabs);
    }

    @Override
    public void init(Minecraft client, int width, int height) {
        super.init(client, width, height);

        int buttonWidth = 128;
        int buttonHeight = 20;
        int centerX = (this.width - buttonWidth) / 2;
        int ySep = 32;

        List<ItemStorage<?>> selection = this.component.storages().values().stream().filter(storage -> storage.isUnlocked() || storage.canUnlock()).collect(Collectors.toList());
        int top = (this.height - buttonHeight - ySep * (selection.size() - 1)) / 2;

        for (int row = 0, size = selection.size(); row < size; row++) {
            ItemStorage<?> storage = selection.get(row);
            ScalableWidget button = this.add(ScalableWidgets.button().x(centerX).y(top + (row * ySep)).width(buttonWidth).height(buttonHeight).text(storage.getName()).primaryAction(this.selectAction(storage)));

            if (this.displayTabs()) {
                button.active = storage.type().id() != this.storage.type().id();
            }
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.render(matrices, mouseX, mouseY, partialTicks);

        if (!this.storage.itemEquipped()) {
            drawCenteredString(matrices, this.fontRenderer, this.label(), this.width / 2, 40, 0xFFFFFF);
        }
    }

    protected PressCallback<ScalableWidget> selectAction(ItemStorage<?> storage) {
        return button -> {
            Packets.serverItemType.send(new ExtendedPacketBuffer(storage).writeInt(this.slot));
            this.onClose();
        };
    }
}
