package soulboundarmory.client.gui.screen;

import cell.client.gui.widget.callback.PressCallback;
import cell.client.gui.widget.scalable.ScalableWidget;
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
    public void init() {
        super.init();

        this.attributes = this.parent.storage.screenAttributes();
        var size = this.attributes.size();
        var start = (this.height - (size - 1) * this.height / 16) / 2;
        var resetButton = this.add(this.resetButton(this.resetAction(attribute)));

        resetButton.active = this.parent.storage.intValue(StatisticType.spentAttributePoints) > 0;

        for (var index = 0; index < size; index++) {
            var attribute = this.attributes.get(index).statistic;
            var add = this.add(this.squareButton((this.width + 182) / 2, start + index * this.height / 16 + 4, Text.of("+"), this.addPointAction(attribute)));
            var remove = this.add(this.squareButton((this.width + 182) / 2 - 20, start + index * this.height / 16 + 4, Text.of("-"), this.removePointAction(attribute)));

            remove.active = attribute.aboveMin();
            add.active = this.parent.storage.intValue(StatisticType.attributePoints) > 0 && attribute.belowMax();
        }
    }

    @Override
    public void render(MatrixStack matrixes, int mouseX, int mouseY, float tickDelta) {
        super.render(matrixes, mouseX, mouseY, tickDelta);

        this.displayPoints(matrixes, this.parent.storage.intValue(StatisticType.attributePoints));

        for (int row = 0, size = this.attributes.size(); row < size; row++) {
            this.drawAttribute(matrixes, this.attributes.get(row).text, row, size);
        }
    }

    public void drawAttribute(MatrixStack stack, Text format, int row, int rows) {
        this.textRenderer.drawWithShadow(stack, format, (this.width - 182) / 2F, this.height(rows, row), 0xFFFFFF);
    }

    protected PressCallback<ScalableWidget> addPointAction(Statistic statistic) {
        return button -> Packets.serverAttribute.send(new ExtendedPacketBuffer(this.parent.storage)
            .writeIdentifier(statistic.type().id())
            .writeInt(hasShiftDown() ? this.parent.storage.intValue(StatisticType.attributePoints) : 1)
        );
    }

    protected PressCallback<ScalableWidget> removePointAction(Statistic statistic) {
        return button -> Packets.serverAttribute.send(new ExtendedPacketBuffer(this.parent.storage)
            .writeIdentifier(statistic.type().id())
            .writeInt(hasShiftDown() ? -statistic.points() : -1)
        );
    }
}
