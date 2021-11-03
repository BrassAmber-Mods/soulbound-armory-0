package net.auoeke.soulboundarmory.network.server;

import net.auoeke.soulboundarmory.network.ExtendedPacketBuffer;
import net.auoeke.soulboundarmory.network.ItemComponentPacket;
import net.minecraft.nbt.NbtCompound;
import net.minecraftforge.fml.network.NetworkEvent;
import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.auoeke.soulboundarmory.capability.soulbound.item.weapon.StaffStorage;

public class C2SSync implements ItemComponentPacket {
    @Override
    public void execute(ExtendedPacketBuffer buffer, NetworkEvent.Context context, ItemStorage<?> storage) {
        NbtCompound tag = buffer.readNbt();

        if (tag.contains("tab")) {
            storage.tab(tag.getInt("tab"));
        }

        if (storage instanceof StaffStorage && tag.contains("spell")) {
            ((StaffStorage) storage).setSpell((tag.getInt("spell")));
        }
    }
}
