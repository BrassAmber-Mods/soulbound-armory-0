package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.network.ItemComponentPacket;

public class C2SReset extends ItemComponentPacket {
    @Override
    public void execute(ItemStorage<?> storage) {
        var identifier = this.message.readIdentifier();

        if (identifier == null) {
            storage.reset();
        } else {
            storage.reset(Category.registry.getValue(identifier));
        }

        // component.sync();
        storage.refresh();
    }
}
