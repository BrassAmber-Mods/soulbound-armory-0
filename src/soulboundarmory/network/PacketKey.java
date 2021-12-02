package soulboundarmory.network;

import net.auoeke.reflect.Constructors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;
import soulboundarmory.SoulboundArmory;

/**
 A key to a registered packet type; used for sending packets.

 @param <T> the message type of packets of the type to which this key corresponds.
 */
public final class PacketKey<T> {
    public final Class<? extends Packet<T>> type;

    PacketKey(Class<? extends Packet<T>> type) {
        this.type = type;
    }

    /**
     Instantiate a packet of the registered type.
     */
    Packet<T> instantiate() {
        return Constructors.instantiate(this.type);
    }

    /**
     Send a message from the server to a client.
     */
    public void send(Entity player, T message) {
        SoulboundArmory.channel.sendTo(this.store(message), ((ServerPlayerEntity) player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    /**
     Send a message from the client to the server.
     */
    @OnlyIn(Dist.CLIENT)
    public void send(T message) {
        SoulboundArmory.channel.sendToServer(this.store(message));
    }

    private Packet<T> store(T message) {
        var packet = this.instantiate();
        packet.store(message);

        return packet;
    }
}
