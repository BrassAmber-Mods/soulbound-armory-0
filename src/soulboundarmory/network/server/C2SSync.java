package soulboundarmory.network.server;

import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.soulbound.item.weapon.StaffStorage;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.ItemComponentPacket;
import net.minecraftforge.fml.network.NetworkEvent;

public class C2SSync implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        var tag = buffer.readNbt();

        if (tag.contains("tab")) {
            storage.tab(tag.getInt("tab"));
        }

        if (storage instanceof StaffStorage staff && tag.contains("spell")) {
            staff.setSpell((tag.getInt("spell")));
        }
    }
}
