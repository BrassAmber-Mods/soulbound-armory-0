package user11681.soulboundarmory.network.server;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;

public class C2SBindSlot implements ServerItemComponentPacket {
    @Override
    public void execute(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, ExtendedPacketBuffer buffer, PacketSender responder, ItemStorage<?> storage) {
        int slot = buffer.readInt();

        if (storage.getBoundSlot() == slot) {
            storage.unbindSlot();
        } else {
            storage.bindSlot(slot);
        }

        //        this.component.sync();
        storage.refresh();
    }
}
