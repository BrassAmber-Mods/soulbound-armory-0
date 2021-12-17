package soulboundarmory.component.statistics.history;

import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.statistics.StatisticType;

public final class AttributeHistory extends History<AttributeRecord> {
    public AttributeHistory(ItemComponent<?> component) {
        super(component);
    }

    public void record(StatisticType attribute, int points) {
        this.record(new AttributeRecord(this.component, attribute, points));
    }

    @Override
    protected AttributeRecord skeleton() {
        return new AttributeRecord(this.component);
    }
}
