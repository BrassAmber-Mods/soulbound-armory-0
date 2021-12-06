package soulboundarmory.component;

import net.minecraft.nbt.NbtCompound;

public interface Component {
    /**
     Serialize this component into `tag`.

     @param tag an empty compound tag for this component
     */
    void serialize(NbtCompound tag);

    /**
     Deserialize this component from `tag`.
     Invoked only if this component has been previously serialized with the entity to which it belongs.

     @param tag a tag containing the same information from the last call to {@link #serialize} on this component
     */
    void deserialize(NbtCompound tag);

    default NbtCompound serialize() {
        var tag = new NbtCompound();
        this.serialize(tag);

        return tag;
    }
}
