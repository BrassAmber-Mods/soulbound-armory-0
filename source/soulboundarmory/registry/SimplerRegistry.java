package soulboundarmory.registry;

import java.util.function.Function;
import com.mojang.serialization.Lifecycle;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.jetbrains.annotations.Nullable;

public class SimplerRegistry<T extends Identifiable> extends SimpleRegistry<T> {
    public SimplerRegistry(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, @Nullable Function<T, RegistryEntry.Reference<T>> valueToEntryFunction) {
        super(key, lifecycle, valueToEntryFunction);
    }

    public <A extends T> A register(A entry) {
        return Registry.register(this, entry.id(), entry);
    }
}
