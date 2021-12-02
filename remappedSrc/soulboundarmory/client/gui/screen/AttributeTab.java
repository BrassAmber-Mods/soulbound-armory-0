package soulboundarmory.client.gui.screen;

import java.util.List;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import cell.client.gui.widget.callback.PressCallback;
import cell.client.gui.widget.scalable.ScalableWidget;
import soulboundarmory.component.statistics.Statistic;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;

import static soulboundarmory.component.statistics.Category.attribute;

import I;

public class AttributeTab extends SoulboundTab {
    protected List<StatisticEntry> attributes;

    public AttributeTab() {
        super(Translations.menuButtonAttributes);
    }

    @Override
    public void init() {
        super.init();

        this.attributes = this.parent.storage.screenAttributes();
        I size = this.attributes.size();
        I start = (this.height - (size - 1) * this.height / 16) / 2;
        ScalableWidget resetButton = this.add(this.resetButton(this.resetAction(attribute)));

        resetButton.active = this.parent.storage.datum(StatisticType.spentAttributePoints) > 0;

        for (I index = 0; index < size; index++) {
            Statistic attribute = this.attributes.get(index).statistic;
            ScalableWidget add = this.add(this.squareButton((this.width + 182) / 2, start + index * this.height / 16 + 4, new LiteralText("+"), this.addPointAction(attribute)));
            ScalableWidget remove = this.add(this.squareButton((this.width + 182) / 2 - 20, start + index * this.height / 16 + 4, new LiteralText("-"), this.removePointAction(attribute)));

            remove.active = attribute.aboveMin();
            add.active = this.parent.storage.datum(StatisticType.attributePoints) > 0 && attribute.belowMax();
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
        super.render(matrices, mouseX, mouseY, tickDelta);

        I points = this.parent.storage.datum(StatisticType.attributePoints);

        if (points > 0) {
            drawCenteredText(matrices, this.textRenderer, String.format("%s: %s", Translations.menuUnspentPoints, points), Math.round(this.width / 2F), 4, 0xFFFFFF);
        }

        for (int row = 0, size = this.attributes.size(); row < size; row++) {
            this.drawAttribute(matrices, this.attributes.get(row).text, row, size);
        }
    }

    public void drawAttribute(MatrixStack stack, Text format, int row, int rows) {
        this.textRenderer.drawWithShadow(stack, format, (this.width - 182) / 2F, this.height(rows, row), 0xFFFFFF);
    }

    protected PressCallback<ScalableWidget> addPointAction(Statistic statistic) {
        return button -> Packets.serverAttribute.send(new ExtendedPacketBuffer(this.parent.storage)
            .writeIdentifier(statistic.type().id())
            .writeInt(hasShiftDown() ? this.parent.storage.datum(StatisticType.attributePoints) : 1)
        );
    }

    protected PressCallback<ScalableWidget> removePointAction(Statistic statistic) {
        return button -> Packets.serverAttribute.send(new ExtendedPacketBuffer(this.parent.storage)
            .writeIdentifier(statistic.type().id())
            .writeInt(hasShiftDown() ? -statistic.points() : -1)
        );
    }
}
