package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.soulbound.item.weapon.StaffStorage;
import soulboundarmory.network.ItemComponentPacket;

public class C2SSync extends ItemComponentPacket {
    @Override
    public void execute(ItemStorage<?> storage) {
        var tag = this.message.readNbt();

        if (tag.contains("tab")) {
            storage.tab(tag.getInt("tab"));
        }

        if (storage instanceof StaffStorage staff && tag.contains("spell")) {
            staff.setSpell((tag.getInt("spell")));
        }
    }
}
