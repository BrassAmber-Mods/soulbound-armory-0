package soulboundarmory.client.gui.screen;

import java.util.List;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.statistics.Statistic;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;

import static soulboundarmory.component.statistics.Category.attribute;

public class AttributeTab extends SoulboundTab {
    protected List<StatisticEntry> attributes;

    public AttributeTab() {
        super(Translations.guiButtonAttributes);
    }

    @Override
    public void initialize() {
        this.attributes = this.parent().item.screenAttributes();
        var size = this.attributes.size();
        var start = (this.height() - (size - 1) * this.height() / 16) / 2;
        this.add(this.resetButton(this.resetAction(attribute))).active(this.parent().item.statistics.get(attribute).values().stream().anyMatch(Statistic::aboveMin));

        for (var index = 0; index < size; index++) {
            var attribute = this.attributes.get(index).statistic;
            this.add(this.squareButton((this.width() + 182) / 2, start + index * this.height() / 16 + 4, "+", this.addPointAction(attribute))).active(this.parent().item.intValue(StatisticType.attributePoints) > 0 && attribute.belowMax());
            this.add(this.squareButton((this.width() + 182) / 2 - 20, start + index * this.height() / 16 + 4, "-", this.removePointAction(attribute))).active(attribute.aboveMin());
        }
    }

    @Override
    public void render(MatrixStack matrixes, int mouseX, int mouseY, float tickDelta) {
        super.render(matrixes, mouseX, mouseY, tickDelta);

        this.displayPoints(matrixes, this.parent().item.intValue(StatisticType.attributePoints));

        for (int row = 0, size = this.attributes.size(); row < size; row++) {
            this.drawAttribute(matrixes, this.attributes.get(row).text, row, size);
        }
    }

    public void drawAttribute(MatrixStack stack, Text format, int row, int rows) {
        textDrawer.drawWithShadow(stack, format, (this.width() - 182) / 2F, this.height(rows, row), 0xFFFFFF);
    }

    protected Runnable addPointAction(Statistic statistic) {
        return () -> Packets.serverAttribute.send(new ExtendedPacketBuffer(this.parent().item)
            .writeIdentifier(statistic.type.id())
            .writeInt(isShiftDown() ? Integer.MAX_VALUE : 1)
        );
    }

    protected Runnable removePointAction(Statistic statistic) {
        return () -> Packets.serverAttribute.send(new ExtendedPacketBuffer(this.parent().item)
            .writeIdentifier(statistic.type.id())
            .writeInt(isShiftDown() ? Integer.MIN_VALUE : -1)
        );
    }
}
