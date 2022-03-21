package soulboundarmory.client.gui.screen;

import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.Statistic;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.util.Util;

public class AttributeTab extends SoulboundTab {
    public AttributeTab() {
        super(Translations.guiButtonAttributes);
    }

    @Override public void initialize() {
        var component = this.container().item();
        var attributeTypes = component.screenAttributes();
        var attributes = attributeTypes.stream().map(component::statistic).toList();
        var length = Math.max(this.container().xpBar.width(), width(attributeTypes.stream().map(component::format)) + 40);
        this.add(this.resetButton(Category.attribute)).active(component.statistics.get(Category.attribute).values().stream().anyMatch(Statistic::aboveMin));
        this.displayPoints(component::attributePoints);

        Util.enumerate(attributes, (statistic, row) -> {
            var x = length / 2;
            var y = this.height(attributes.size(), row) + 4;
            this.add(this.squareButton(x - 20, y, "-", () -> this.removePoint(statistic))).active(statistic::aboveMin);
            this.add(this.squareButton(x, y, "+", () -> this.addPointAction(statistic))).active(() -> component.attributePoints() > 0 && statistic.belowMax());
            this.text(widget -> widget.shadow().x(.5, -length / 2).y(this.height(attributes.size(), row)).text(() -> component.format(statistic.type)));
        });
    }

    protected void addPointAction(Statistic statistic) {
        Packets.serverAttribute.send(new ExtendedPacketBuffer(this.container().item())
            .writeIdentifier(statistic.type.id())
            .writeInt(isShiftDown() ? Integer.MAX_VALUE : 1)
        );
    }

    protected void removePoint(Statistic statistic) {
        Packets.serverAttribute.send(new ExtendedPacketBuffer(this.container().item())
            .writeIdentifier(statistic.type.id())
            .writeInt(isShiftDown() ? Integer.MIN_VALUE : -1)
        );
    }
}
