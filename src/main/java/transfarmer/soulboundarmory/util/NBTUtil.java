package transfarmer.soulboundarmory.util;

import net.minecraft.nbt.NBTTagCompound;

import java.util.Set;
import java.util.function.Consumer;

public class NBTUtil {
    public static void ifHasKeyTag(final NBTTagCompound tag, final String key, final Consumer<NBTTagCompound> consumer) {
        if (tag.hasKey(key)) {
            consumer.accept(tag.getCompoundTag(key));
        }
    }

    public static void ifNonNull(final NBTTagCompound tag, final String key, final Consumer<Set<String>> consumer) {
        if (tag.hasKey(key)) {
            consumer.accept(tag.getKeySet());
        }
    }
}
