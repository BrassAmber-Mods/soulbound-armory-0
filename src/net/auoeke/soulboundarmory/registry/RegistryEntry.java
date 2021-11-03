package net.auoeke.soulboundarmory.registry;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import user11681.reflect.Accessor;
import user11681.reflect.Constructors;
import user11681.reflect.Fields;
import net.auoeke.soulboundarmory.SoulboundArmory;
import net.auoeke.soulboundarmory.util.Util;

@SuppressWarnings("unchecked")
public abstract class RegistryEntry<T extends RegistryEntry<T>> implements IForgeRegistryEntry<T> {
    private static final Map<Class<? extends RegistryEntry<?>>, IForgeRegistry<? extends RegistryEntry<?>>> registries = new Reference2ReferenceOpenHashMap<>();

    private Identifier id;

    protected static <T extends RegistryEntry<T>> T register(String name, T... dummy) {
        return register(SoulboundArmory.id(name), dummy);
    }

    protected static <T extends RegistryEntry<T>> T register(Identifier name, T... dummy) {
        T entry = Constructors.construct(Util.componentType(dummy)).setRegistryName(name);

        ((IForgeRegistry<T>) registries.computeIfAbsent((Class<T>) entry.getClass(), type -> (IForgeRegistry<T>) Accessor.getObject(Fields
            .allStaticFields(type)
            .stream()
            .filter(field -> IForgeRegistry.class.isAssignableFrom(field.getType()))
            .findAny()
            .orElseThrow(NoSuchFieldError::new)
        ))).register(entry);

        return entry;
    }

    public T setRegistryName(String path) {
        return this.setRegistryName(SoulboundArmory.id(path));
    }

    @Override
    public T setRegistryName(Identifier id) {
        this.id = id;

        return (T) this;
    }

    @Nullable
    @Override
    public Identifier getRegistryName() {
        return this.id;
    }

    public Identifier id() {
        return this.id;
    }

    @Override
    public Class<T> getRegistryType() {
        return (Class<T>) this.getClass();
    }

    public String string() {
        return this.id.toString();
    }
}
