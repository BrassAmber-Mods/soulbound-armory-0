package soulboundarmory.network;

import net.minecraft.network.PacketBuffer;

public interface BufferPacket extends Packet<ExtendedPacketBuffer> {
    @Override
    default void write(ExtendedPacketBuffer message, PacketBuffer buffer) {
        buffer.writeBytes(message.readBytes(message.readableBytes()));
    }

    @Override
    default ExtendedPacketBuffer read(PacketBuffer buffer) {
        return new ExtendedPacketBuffer(buffer.copy());
    }
}
