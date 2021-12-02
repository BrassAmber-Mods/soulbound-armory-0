package soulboundarmory.network;

import net.minecraft.network.PacketBuffer;

/**
 * A packet whose message is an {@link ExtendedPacketBuffer}.
 */
public abstract class BufferPacket extends Packet<ExtendedPacketBuffer> {
    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeBytes(this.message.array());
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.message = new ExtendedPacketBuffer(buffer.copy());
    }
}
