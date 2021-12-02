package soulboundarmory.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import soulboundarmory.SoulboundArmoryClient;

/**
 The base packet type.

 Packets are message containers and handlers. They are constructed internally when sending and receiving messages.

 @param <T> the type of the message that may be stored in this packet.
 */
public abstract class Packet<T> {
    protected NetworkEvent.Context context;

    public final void execute(NetworkEvent.Context context) {
        this.context = context;
        this.execute();
    }

    /**
     If this method is invoked by the server, then return the sender of this packet; otherwise, return the player.
     */
    protected final PlayerEntity player() {
        return this.context.getDirection().getReceptionSide().isClient() ? SoulboundArmoryClient.client.player : this.context.getSender();
    }

    /**
     Store a message in this packet for later use in writing to a buffer.
     */
    public abstract void store(T message);

    /**
     Write this packet's message to a buffer.
     */
    public abstract void write(PacketBuffer buffer);

    /**
     Read the message in a buffer.
     */
    public abstract void read(PacketBuffer buffer);

    /**
     After the message has been read, perform some action with it.
     */
    protected abstract void execute();
}
