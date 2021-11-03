package user11681.soulboundarmory.network;

import net.minecraft.network.PacketByteBuf;

public interface SimplePacket extends Packet<ExtendedPacketBuffer> {
    @Override
    default void write(ExtendedPacketBuffer message, PacketByteBuf buffer) {
        buffer.writeBytes(message.readBytes(message.readableBytes()));
    }

    @Override
    default ExtendedPacketBuffer read(PacketByteBuf buffer) {
        return new ExtendedPacketBuffer(buffer.copy());
    }
}
