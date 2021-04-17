package user11681.soulboundarmory.network.server;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;

public class C2SItemType implements ServerItemComponentPacket {
    @Override
    public void execute(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, ExtendedPacketBuffer buffer, PacketSender responder, ItemStorage<?> storage) {
        player.getInventory().setStack(buffer.readInt(), storage.getItemStack());
        storage.getComponent().setCurrentItem(storage);
        storage.removeOtherItems();
        storage.sync();
    }
}
