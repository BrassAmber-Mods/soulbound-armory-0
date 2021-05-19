package user11681.soulboundarmory.client.gui.screen.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.capability.statistics.Statistic;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.registry.Packets;

import static user11681.soulboundarmory.capability.statistics.Category.attribute;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attributePoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.spentAttributePoints;

public class AttributeTab extends SoulboundTab {
    protected List<StatisticEntry> attributes;

    public AttributeTab(SoulboundCapability component, List<ScreenTab> tabs) {
        super(Translations.menuButtonAttributes, component, tabs);
    }

    @Override
    public void init(Minecraft client, int width, int height) {
        super.init(client, width, height);

        this.attributes = this.storage.getScreenAttributes();
        int size = this.attributes.size();
        int start = (this.height - (size - 1) * this.height / 16) / 2;
        ScalableWidget resetButton = this.add(this.resetButton(this.resetAction(attribute)));

        resetButton.active = this.storage.getDatum(spentAttributePoints) > 0;

        for (int index = 0; index < size; index++) {
            Statistic attribute = this.attributes.get(index).statistic();
            ButtonWidget add = this.addButton(this.squareButton((this.width + 182) / 2, start + index * this.height / 16 + 4, new StringTextComponent("+"), this.addPointAction(attribute)));
            ButtonWidget remove = this.addButton(this.squareButton((this.width + 182) / 2 - 20, start + index * this.height / 16 + 4, new StringTextComponent("-"), this.removePointAction(attribute)));

            remove.active = attribute.isAboveMin();
            add.active = this.storage.getDatum(attributePoints) > 0 && attribute.isBelowMax();
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
        super.render(matrices, mouseX, mouseY, tickDelta);

        int points = this.storage.getDatum(attributePoints);

        if (points > 0) {
            drawCenteredString(matrices, this.textRenderer, String.format("%s: %s", Translations.menuUnspentPoints, points), Math.round(width / 2F), 4, 0xFFFFFF);
        }

        for (int row = 0, size = this.attributes.size(); row < size; row++) {
            this.drawAttribute(matrices, this.attributes.get(row).text(), row, size);
        }
    }

    public void drawAttribute(MatrixStack stack, ITextComponent format, int row, int rows) {
        textRenderer.draw(stack, format, (this.width - 182) / 2F, this.getHeight(rows, row), 0xFFFFFF);
    }

    protected PressAction addPointAction(Statistic statistic) {
        return (ButtonWidget button) ->
            Packets.serverAttribute.send(new ExtendedPacketBuffer(this.storage).writeResourceLocation(statistic.type().id()).writeInt(hasShiftDown() ? this.storage.getDatum(attributePoints) : 1));
    }

    protected PressAction removePointAction(Statistic statistic) {
        return (ButtonWidget button) ->
            Packets.serverAttribute.send(new ExtendedPacketBuffer(this.storage).writeResourceLocation(statistic.type().id()).writeInt(-(hasShiftDown() ? statistic.getPoints() : 1)));
    }
}
