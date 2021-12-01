package soulboundarmory.util;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.INameMappingService;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import net.auoeke.reflect.Classes;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.util.TriConsumer;
import soulboundarmory.SoulboundArmory;

public class Util {
    private static BiFunction<INameMappingService.Domain, String, String> mapper;

    public static <T> T nul() {
        return null;
    }

    public static <T> T cast(Object object) {
        return (T) object;
    }

    public static MinecraftServer server() {
        return LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
    }

    public static boolean contains(Object target, Object... items) {
        return Arrays.asList(items).contains(target);
    }

    @SafeVarargs
    public static <T> HashSet<T> hashSet(T... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }

    @SafeVarargs
    public static <T> Class<T> componentType(T... array) {
        return (Class<T>) array.getClass().getComponentType();
    }

    @SafeVarargs
    public static <T extends IForgeRegistryEntry<T>> IForgeRegistry<T> registry(String path, T... dummy) {
        return new RegistryBuilder<T>().setType(componentType(dummy)).setName(SoulboundArmory.id(path)).create();
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
