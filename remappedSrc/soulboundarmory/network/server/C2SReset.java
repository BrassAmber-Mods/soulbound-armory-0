package soulboundarmory.network.server;

import net.minecraft.util.Identifier;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.network.ItemComponentPacket;

public class C2SReset extends ItemComponentPacket {
    @Override
    public void execute(ItemStorage<?> storage) {
        Identifier identifier = this.message.readIdentifier();

        if (identifier != null) {
            storage.reset(Category.registry.getValue(identifier));
        } else {
            storage.reset();
        }

        // component.sync();
        storage.refresh();
    }
}
