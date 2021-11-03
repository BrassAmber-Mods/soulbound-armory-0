package net.auoeke.soulboundarmory.network;

import net.auoeke.soulboundarmory.SoulboundArmory;
import net.auoeke.soulboundarmory.SoulboundArmoryClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public interface Packet<T> {
    void execute(T message, NetworkEvent.Context context);

    void write(T message, PacketByteBuf buffer);

    T read(PacketByteBuf buffer);

    default PlayerEntity player(NetworkEvent.Context context) {
        return context.getDirection().getReceptionSide().isClient() ? SoulboundArmoryClient.client.player : context.getSender();
    }

    default void send(Entity player, T message) {
        SoulboundArmory.channel.sendTo(message, ((ServerPlayerEntity) player).networkHandler.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    @OnlyIn(Dist.CLIENT)
    default PlayerEntity player() {
        return SoulboundArmoryClient.client.player;
    }

    @OnlyIn(Dist.CLIENT)
    default void send(T message) {
        SoulboundArmory.channel.sendToServer(message);
    }
}
