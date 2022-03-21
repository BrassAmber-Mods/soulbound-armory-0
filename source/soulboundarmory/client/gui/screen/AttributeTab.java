package soulboundarmory.client.gui.screen;

import java.util.Map;
import net.minecraft.text.Text;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.Statistic;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.util.Util;

public class AttributeTab extends SoulboundTab {
    protected Map<Statistic, Text> attributes;
    protected int length;

    public AttributeTab() {
        super(Translations.guiButtonAttributes);
    }

    @Override public void initialize() {
        this.attributes = this.container().item().screenAttributes();
        this.length = Math.max(this.container().xpBar.width(), width(this.attributes.values().stream()) + 40);
        this.add(this.resetButton(Category.attribute)).active(this.container().item().statistics.get(Category.attribute).values().stream().anyMatch(Statistic::aboveMin));

        Util.enumerate(this.attributes, (statistic, text, row) -> {
            var x = this.middleX() + this.length / 2;
            var y = this.height(this.attributes.size(), row) + 4;
            this.add(this.squareButton(x - 20, y, "-", this.removePointAction(statistic))).active(statistic.aboveMin());
            this.add(this.squareButton(x, y, "+", this.addPointAction(statistic))).active(this.container().item().attributePoints() > 0 && statistic.belowMax());
        });
    }

    protected Runnable addPointAction(Statistic statistic) {
        return () -> Packets.serverAttribute.send(new ExtendedPacketBuffer(this.container().item())
            .writeIdentifier(statistic.type.id())
            .writeInt(isShiftDown() ? Integer.MAX_VALUE : 1)
        );
    }

    protected Runnable removePointAction(Statistic statistic) {
        return () -> Packets.serverAttribute.send(new ExtendedPacketBuffer(this.container().item())
            .writeIdentifier(statistic.type.id())
            .writeInt(isShiftDown() ? Integer.MIN_VALUE : -1)
        );
    }

    @Override protected void render() {
        this.displayPoints(this.container().item().attributePoints());
        Util.enumerate(this.attributes, (entry, text, row) -> textRenderer.drawWithShadow(this.matrixes, text, this.middleX() - this.length / 2F, this.height(this.attributes.size(), row), 0xFFFFFF));
    }
}
