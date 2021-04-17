package user11681.soulboundarmory.network.server;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.statistics.Category;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;

public class C2SReset implements ServerItemComponentPacket {
    @Override
    public void execute(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, ExtendedPacketBuffer buffer, PacketSender responder, ItemStorage<?> storage) {
        Identifier identifier = buffer.readIdentifier();

        if (identifier != null) {
            Category category = Category.category.get(identifier);

            storage.reset(category);
        } else {
            storage.reset();
        }

        //        component.sync();
        storage.refresh();
    }
}
