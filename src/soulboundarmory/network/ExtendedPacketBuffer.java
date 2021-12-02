package soulboundarmory.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import soulboundarmory.component.soulbound.item.ItemStorage;
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

    @Override
    public ExtendedPacketBuffer writeBoolean(boolean value) {
        super.writeBoolean(value);

        return this;
    }

    @Override
    public ResourceLocation readResourceLocation() {
        return new ResourceLocation(this.readString());
    }

    @Override
    public ExtendedPacketBuffer writeResourceLocation(ResourceLocation identifier) {
        return this.writeString(identifier.toString());
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
    public ExtendedPacketBuffer writeUniqueId(UUID id) {
        return (ExtendedPacketBuffer) super.writeUniqueId(id);
    }

    @Override
    public ExtendedPacketBuffer writeInt(int value) {
        super.writeInt(value);

        return this;
    }

    public ExtendedPacketBuffer writeEntity(Entity entity) {
        return this.writeUniqueId(entity.getUniqueID());
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
    public ExtendedPacketBuffer writeCompoundTag(CompoundNBT tag) {
        super.writeCompoundTag(tag);

        return this;
    }
}
