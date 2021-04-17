package user11681.usersmanual.nbt;

import net.minecraft.nbt.CompoundTag;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class NBTUtil {
    public static void ifHasKeyTag(final CompoundTag tag, final String key, final Consumer<CompoundTag> consumer) {
        if (tag.contains(key)) {
            consumer.accept(tag.getCompound(key));
        }
    }

    public static void ifNonNull(final CompoundTag tag, final String key, final Consumer<Set<String>> consumer) {
        if (tag.contains(key)) {
            consumer.accept(tag.getKeys());
        }
    }

    public static void clear(final CompoundTag tag) {
        for (final String key : new HashSet<>(tag.getKeys())) {
            tag.remove(key);
        }
    }
}
