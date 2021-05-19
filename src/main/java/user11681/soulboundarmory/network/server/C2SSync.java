package user11681.soulboundarmory.network.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkEvent;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.soulbound.item.weapon.StaffStorage;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.network.ItemComponentPacket;

public class C2SSync implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        CompoundNBT tag = buffer.readNbt();

        if (tag.contains("tab")) {
            storage.currentTab(tag.getInt("tab"));
        }

        if (storage instanceof StaffStorage && tag.contains("spell")) {
            ((StaffStorage) storage).setSpell((tag.getInt("spell")));
        }
    }
}
