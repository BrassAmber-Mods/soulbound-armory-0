package soulboundarmory.network;

import net.minecraft.network.PacketBuffer;

/**
 * A packet whose message is an {@link ExtendedPacketBuffer}.
 */
public abstract class BufferPacket extends Packet<ExtendedPacketBuffer> {
    protected ExtendedPacketBuffer buffer;

    @Override
    public void store(ExtendedPacketBuffer message) {
        this.buffer = message;
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeBytes(this.buffer.array());
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.buffer = new ExtendedPacketBuffer(buffer.copy());
    }
}
