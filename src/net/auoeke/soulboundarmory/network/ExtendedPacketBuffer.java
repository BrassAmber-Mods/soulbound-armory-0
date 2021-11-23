package net.auoeke.soulboundarmory.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class ExtendedPacketBuffer extends PacketBuffer {
    public ExtendedPacketBuffer() {
        super(Unpooled.buffer());
    }

    public ExtendedPacketBuffer(ByteBuf buffer) {
        super(buffer);
    }

    public ExtendedPacketBuffer(ItemStorage<?> component) {
        this();

        this.writeResourceLocation(component.type().id());
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
    public ExtendedPacketBuffer writeResourceLocation(ResourceLocation identifier) {
        super.writeResourceLocation(identifier);

        return this;
    }

    @Override
    public String readUtf() {
        return this.readCharSequence(this.readInt(), StandardCharsets.UTF_8).toString();
    }

    @Override
    public ExtendedPacketBuffer writeUtf(String string) {
        this.writeInt(string.length());
        this.writeCharSequence(string, StandardCharsets.UTF_8);

        return this;
    }

    @Override
    public ExtendedPacketBuffer writeItem(ItemStack itemStack) {
        return (ExtendedPacketBuffer) super.writeItemStack(itemStack, false);
    }

    @Override
    public ExtendedPacketBuffer writeUUID(UUID id) {
        return (ExtendedPacketBuffer) super.writeUUID(id);
    }

    @Override
    public ExtendedPacketBuffer writeInt(int value) {
        super.writeInt(value);

        return this;
    }

    public ExtendedPacketBuffer writeEntity(Entity entity) {
        return this.writeUUID(entity.getUUID());
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
    public ExtendedPacketBuffer writeNbt(CompoundNBT tag) {
        super.writeNbt(tag);

        return this;
    }
}
