package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.network.ItemComponentPacket;

public final class C2SReset extends ItemComponentPacket {
    @Override
    public void execute(ItemComponent<?> storage) {
        var identifier = this.message.readIdentifier();

        if (identifier == null) {
            storage.reset();
        } else {
            storage.reset(Category.registry.getValue(identifier));
        }

        storage.component.refresh();
    }
}
