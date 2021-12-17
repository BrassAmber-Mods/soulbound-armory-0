package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.network.ItemComponentPacket;

/**
 A client-to-server packet that is sent when a client requests to reset a category.
 */
public final class C2SReset extends ItemComponentPacket {
    @Override
    public void execute(ItemComponent<?> component) {
        var category = this.message.readRegistryEntry(Category.registry);

        if (category == null) {
            component.reset();
        } else {
            component.reset(category);
        }
    }
}
