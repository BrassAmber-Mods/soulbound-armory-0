package soulboundarmory.registry;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.auoeke.reflect.Constructors;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import soulboundarmory.util.Util;

public class ModRegistry<T extends IForgeRegistryEntry<T>> implements IForgeRegistry<T> {
    public final Identifier id;
    public final Class<T> type;
    public final RegistryKey<Registry<T>> key;

    private final Map<Identifier, T> entries = new Object2ReferenceOpenHashMap<>();
    private final Map<RegistryKey<T>, T> keyEntries = new Reference2ReferenceOpenHashMap<>();

    public ModRegistry(Identifier id, Class<T> type) {
        this.id = id;
        this.type = type;
        this.key = RegistryKey.ofRegistry(this.id);
    }

    public ModRegistry(Identifier id, T... dummy) {
        this(id, Util.componentType(dummy));
    }

    public ModRegistry(String path, Class<T> type) {
        this(Util.id(path), type);
    }

    public ModRegistry(String path, T... dummy) {
        this(Util.id(path), Util.componentType(dummy));
    }

    public T register(Identifier id, T entry) {
        this.entries.put(id, entry);
        this.keyEntries.put(RegistryKey.of(this.key, id), entry);

        return entry;
    }

    public T register(String path, T entry) {
        var id = new Identifier(ModLoadingContext.get().getActiveNamespace(), path);
        return this.register(id, entry.setRegistryName(id));
    }

    public T register(String path, Object... arguments) {
        return this.register(path, Constructors.construct(this.type, Util.add(path, arguments)));
    }

    @Override
    public Identifier getRegistryName() {
        return this.id;
    }

    @Override
    public Class<T> getRegistrySuperType() {
        return this.type;
    }

    @Override
    public void register(T entry) {
        this.register(entry.getRegistryName(), entry);
    }

    @Override
    public void registerAll(T... entry) {
        Stream.of(entry).forEach(this::register);
    }

    @Override
    public boolean containsKey(Identifier id) {
        return this.entries.containsKey(id);
    }

    @Override
    public boolean containsValue(T entry) {
        return this.entries.containsValue(entry);
    }

    @Override
    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    @Override
    public T getValue(Identifier id) {
        return this.entries.get(id);
    }

    @Override
    public Identifier getKey(T entry) {
        return entry == null ? this.getDefaultKey() : entry.getRegistryName();
    }

    @Override
    public Identifier getDefaultKey() {
        return null;
    }

    @Override
    public Optional<RegistryKey<T>> getResourceKey(T entry) {
        return Optional.empty();
    }

    @Override
    public Set<Identifier> getKeys() {
        return this.entries.keySet();
    }

    @Override
    public Collection<T> getValues() {
        return this.entries.values();
    }

    @Override
    public Set<Map.Entry<RegistryKey<T>, T>> getEntries() {
        return this.keyEntries.entrySet();
    }

    @Override
    public <A> A getSlaveMap(Identifier arg, Class<A> type) {
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return this.getValues().iterator();
    }
}
