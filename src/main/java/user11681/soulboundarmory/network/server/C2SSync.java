package user11681.soulboundarmory.network.server;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.item.weapon.StaffStorage;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;

public class C2SSync implements ServerItemComponentPacket {
    @Override
    public void execute(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, ExtendedPacketBuffer buffer, PacketSender responder, ItemStorage<?> storage) {
        NbtCompound tag = buffer.readNbt();

        if (tag.contains("tab")) {
            storage.setCurrentTab(tag.getInt("tab"));
        }

        if (storage instanceof StaffStorage && tag.contains("spell")) {
            ((StaffStorage) storage).setSpell((tag.getInt("spell")));
        }
    }
}
