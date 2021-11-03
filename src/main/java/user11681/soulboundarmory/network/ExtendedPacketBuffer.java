package user11681.soulboundarmory.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;

public class ExtendedPacketBuffer extends PacketByteBuf {
    public ExtendedPacketBuffer() {
        super(Unpooled.buffer());
    }

    public ExtendedPacketBuffer(ByteBuf buffer) {
        super(buffer);
    }

    public ExtendedPacketBuffer(ItemStorage<?> component) {
        this();

        this.writeIdentifier(component.type().id());
    }

    public static ExtendedPacketBuffer copy(ByteBuf buffer) {
        return new ExtendedPacketBuffer(buffer.copy());
    }

    @Override
    public ExtendedPacketBuffer writeBoolean(boolean value) {
        super.writeBoolean(value);

        return this;
    }

    @Override
    public ExtendedPacketBuffer writeIdentifier(Identifier identifier) {
        super.writeIdentifier(identifier);

        return this;
    }

    @Override
    public String readString() {
        return this.readCharSequence(this.readInt(), StandardCharsets.UTF_8).toString();
    }

    @Override
    public ExtendedPacketBuffer writeString(String string) {
        this.writeInt(string.length());
        this.writeCharSequence(string, StandardCharsets.UTF_8);

        return this;
    }

    @Override
    public ExtendedPacketBuffer writeItemStack(ItemStack itemStack) {
        return (ExtendedPacketBuffer) super.writeItemStack(itemStack, false);
    }

    @Override
    public ExtendedPacketBuffer writeUuid(UUID id) {
        return (ExtendedPacketBuffer) super.writeUuid(id);
    }

    @Override
    public ExtendedPacketBuffer writeInt(int value) {
        super.writeInt(value);

        return this;
    }

    public ExtendedPacketBuffer writeEntity(Entity entity) {
        return this.writeUuid(entity.getUuid());
    }

    //    public Entity readEntity() {
    //        return EntityUtil.getEntity(this.readUuid());
    //    }

    public ExtendedPacketBuffer writePlayer(Entity player) {
        return this.writeEntity(player);
    }

    //    public PlayerEntity readPlayer() {
    //        return (PlayerEntity) this.readEntity();
    //    }

    @Override
    public ExtendedPacketBuffer writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        return this;
    }
}
