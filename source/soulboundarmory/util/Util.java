package soulboundarmory.util;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.INameMappingService;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import net.auoeke.reflect.Classes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.util.TriConsumer;

public class Util {
    private static final Map<Class<?>, IForgeRegistry<?>> registries = new Reference2ReferenceOpenHashMap<>();
    private static BiFunction<INameMappingService.Domain, String, String> mapper;

    public static <T> T cast(Object object) {
        return (T) object;
    }

    public static MinecraftServer server() {
        return LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
    }

    public static boolean containsIgnoreCase(String string, String substring) {
        return Pattern.compile(Pattern.quote(substring), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(string).find();
    }

    public static boolean contains(Object target, Object... items) {
        return Arrays.asList(items).contains(target);
    }

    public static <T> Set<T> hashSet(T... elements) {
        return new ObjectOpenHashSet<>(elements);
    }

    public static <T> Class<T> componentType(T... array) {
        return (Class<T>) array.getClass().getComponentType();
    }

    public static <A> A[] add(A element, A[] array) {
        var union = Arrays.copyOf(array, array.length + 1);
        union[array.length] = element;

        return union;
    }

    public static Identifier id(String path) {
        return new Identifier(ModLoadingContext.get().getActiveNamespace(), path);
    }

    public static void ifPresent(NbtCompound tag, String key, Consumer<NbtCompound> action) {
        var child = (NbtCompound) tag.get(key);

        if (child != null) {
            action.accept(child);
        }
    }

    public static <T extends IForgeRegistryEntry<T>> IForgeRegistry<T> newRegistry(String path, T... dummy) {
        return new RegistryBuilder<T>().setName(id(path)).setType(componentType(dummy)).create();
    }

    public static <T extends IForgeRegistryEntry<T>> IForgeRegistry<T> registry(Class<T> type) {
        if (type == null) {
            return null;
        }

        return (IForgeRegistry<T>) registries.computeIfAbsent(type, __ -> {
            var registry = RegistryManager.ACTIVE.getRegistry(type);
            return registry == null ? registry(cast(type.getSuperclass())) : registry;
        });
    }

    public static <K, V> void enumerate(Map<K, V> map, TriConsumer<K, V, Integer> action) {
        var counter = 0;

        for (var entry : map.entrySet()) {
            action.accept(entry.getKey(), entry.getValue(), counter++);
        }
    }

    public static List<Type> arguments(Class<?> subtype, Class<?> supertype) {
        for (var type : Classes.supertypes(subtype)) {
            if (type == supertype) {
                for (var genericType : Classes.genericSupertypes(subtype)) {
                    if (genericType.equals(supertype)) {
                        return Arrays.asList(((ParameterizedType) genericType).getActualTypeArguments());
                    }
                }
            }
        }

        return null;
    }

    public static String mapClass(String name) {
        return mapper().apply(INameMappingService.Domain.CLASS, name);
    }

    public static String mapMethod(String name) {
        return mapper().apply(INameMappingService.Domain.METHOD, name);
    }

    public static String mapField(String name) {
        return mapper().apply(INameMappingService.Domain.FIELD, name);
    }

    private static BiFunction<INameMappingService.Domain, String, String> mapper() {
        return mapper == null ? mapper = Launcher.INSTANCE.environment().findNameMapping("srg").get() : mapper;
    }
}
