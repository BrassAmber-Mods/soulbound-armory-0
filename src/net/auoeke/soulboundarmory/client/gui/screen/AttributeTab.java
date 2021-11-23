package net.auoeke.soulboundarmory.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import net.auoeke.cell.client.gui.widget.callback.PressCallback;
import net.auoeke.cell.client.gui.widget.scalable.ScalableWidget;
import net.auoeke.soulboundarmory.capability.statistics.Statistic;
import net.auoeke.soulboundarmory.capability.statistics.StatisticType;
import net.auoeke.soulboundarmory.client.i18n.Translations;
import net.auoeke.soulboundarmory.network.ExtendedPacketBuffer;
import net.auoeke.soulboundarmory.registry.Packets;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import static net.auoeke.soulboundarmory.capability.statistics.Category.attribute;

public class AttributeTab extends SoulboundTab {
    protected List<StatisticEntry> attributes;

    public AttributeTab() {
        super(Translations.menuButtonAttributes);
    }

    @Override
    public void init() {
        super.init();

        this.attributes = this.parent.storage.screenAttributes();
        var size = this.attributes.size();
        var start = (this.height - (size - 1) * this.height / 16) / 2;
        var resetButton = this.add(this.resetButton(this.resetAction(attribute)));

        resetButton.active = this.parent.storage.datum(StatisticType.spentAttributePoints) > 0;

        for (var index = 0; index < size; index++) {
            var attribute = this.attributes.get(index).statistic;
            var add = this.add(this.squareButton((this.width + 182) / 2, start + index * this.height / 16 + 4, new StringTextComponent("+"), this.addPointAction(attribute)));
            var remove = this.add(this.squareButton((this.width + 182) / 2 - 20, start + index * this.height / 16 + 4, new StringTextComponent("-"), this.removePointAction(attribute)));

            remove.active = attribute.isAboveMin();
            add.active = this.parent.storage.datum(StatisticType.attributePoints) > 0 && attribute.isBelowMax();
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
        super.render(matrices, mouseX, mouseY, tickDelta);

        var points = this.parent.storage.datum(StatisticType.attributePoints);

        if (points > 0) {
            drawCenteredString(matrices, this.font, String.format("%s: %s", Translations.menuUnspentPoints, points), Math.round(this.width / 2F), 4, 0xFFFFFF);
        }

        for (int row = 0, size = this.attributes.size(); row < size; row++) {
            this.drawAttribute(matrices, this.attributes.get(row).text, row, size);
        }
    }

    public void drawAttribute(MatrixStack stack, ITextComponent format, int row, int rows) {
        this.font.draw(stack, format, (this.width - 182) / 2F, this.height(rows, row), 0xFFFFFF);
    }

    protected PressCallback<ScalableWidget> addPointAction(Statistic statistic) {
        return button -> Packets.serverAttribute.send(new ExtendedPacketBuffer(this.parent.storage)
            .writeResourceLocation(statistic.type().id())
            .writeInt(hasShiftDown() ? this.parent.storage.datum(StatisticType.attributePoints) : 1)
        );
    }

    protected PressCallback<ScalableWidget> removePointAction(Statistic statistic) {
        return button -> Packets.serverAttribute.send(new ExtendedPacketBuffer(this.parent.storage)
            .writeResourceLocation(statistic.type().id())
            .writeInt(hasShiftDown() ? -statistic.getPoints() : -1)
        );
    }
}
