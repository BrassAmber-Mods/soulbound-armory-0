package soulboundarmory.network;

import net.minecraft.network.PacketByteBuf;

/**
 * A packet whose message is an {@link ExtendedPacketBuffer}.
 */
public abstract class BufferPacket extends Packet<ExtendedPacketBuffer> {
    @Override
    public void write(PacketByteBuf buffer) {
        buffer.writeBytes(this.message.array());
    }

    @Override
    public void read(PacketByteBuf buffer) {
        this.message = new ExtendedPacketBuffer(buffer.copy());
    }
}
