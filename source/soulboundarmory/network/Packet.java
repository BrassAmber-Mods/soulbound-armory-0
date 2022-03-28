package soulboundarmory.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import soulboundarmory.module.gui.widget.Widget;

/**
 The base packet type.

 Packets are message containers and handlers. They are constructed internally when sending and receiving messages.

 @param <T> the type of the message that may be stored in this packet */
public abstract class Packet<T> {
    protected NetworkEvent.Context context;
    protected T message;

    /**
     Write this packet's message to a buffer.
     */
    public abstract void write(PacketByteBuf buffer);

    /**
     Read the message in a buffer.
     */
    public abstract void read(PacketByteBuf buffer);

    public final void execute(NetworkEvent.Context context) {
        this.context = context;
        this.execute();
        context.setPacketHandled(true);
    }

    /**
     After the message has been read, perform some action with it.
     */
    protected abstract void execute();

    /**
     If this method is in a server context, then return the sender of this packet; otherwise, return the player.
     */
    protected final PlayerEntity player() {
        return this.context.getDirection().getReceptionSide().isClient() ? player0() : this.context.getSender();
    }

    @OnlyIn(Dist.CLIENT)
    private static PlayerEntity player0() {
        return Widget.player();
    }
}
