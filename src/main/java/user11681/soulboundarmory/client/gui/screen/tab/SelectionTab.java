package user11681.soulboundarmory.client.gui.screen.tab;

import java.util.List;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.registry.Packets;
import user11681.usersmanual.client.gui.screen.ScreenTab;

public class SelectionTab extends SoulboundTab {
    public SelectionTab(ITextComponent title, final SoulboundCapability component, final List<ScreenTab> tabs) {
        super(title, component, tabs);
    }

    @Override
    public void init(Minecraft client, final int width, final int height) {
        super.init(client, width, height);

        final int buttonWidth = 128;
        final int buttonHeight = 20;
        final int centerX = (this.width - buttonWidth) / 2;
        final int ySep = 32;

        final List<ItemStorage<?>> selection = this.component.storages().values();

        selection.removeIf((ItemStorage<?> storage) -> !storage.isUnlocked() && !storage.canUnlock());

        final int top = (this.height - buttonHeight - ySep * (selection.size() - 1)) / 2;

        for (int row = 0, size = selection.size(); row < size; row++) {
            final ItemStorage<?> storage = selection.get(row);
            final ButtonWidget button = this.addButton(new ButtonWidget(centerX, top + (row * ySep), buttonWidth, buttonHeight, storage.getName(), this.selectAction(storage)));

            if (this.displayTabs()) {
                button.active = storage.getType().getIdentifier() != this.storage.getType().getIdentifier();
            }
        }
    }

    @Override
    public void render(MatrixStack matrices, final int mouseX, final int mouseY, final float partialTicks) {
        super.render(matrices, mouseX, mouseY, partialTicks);

        if (!this.storage.itemEquipped()) {
            this.drawCenteredString(matrices, this.textRenderer, this.getLabel().toString(), this.width / 2, 40, 0xFFFFFF);
        }
    }

    protected PressAction selectAction(ItemStorage<?> storage) {
        return (ButtonWidget button) -> {
            ClientPlayNetworking.send(Packets.serverItemType, new ExtendedPacketBuffer(storage).writeInt(this.slot));
            this.onClose();
        };
    }
}
