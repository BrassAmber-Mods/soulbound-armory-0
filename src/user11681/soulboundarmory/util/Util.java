package user11681.soulboundarmory.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.util.TriConsumer;
import user11681.reflect.Classes;
import user11681.soulboundarmory.SoulboundArmory;

public class Util {
    public static MinecraftServer server() {
        return LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
    }

    public static <T> T nul() {
        return null;
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
        int counter = 0;

        for (Map.Entry<K, V> entry : map.entrySet()) {
            action.accept(entry.getKey(), entry.getValue(), counter++);
        }
    }

    public static List<Type> arguments(Class<?> subtype, Class<?> supertype) {
        for (Class<?> type : Classes.supertypes(subtype)) {
            if (type == supertype) {
                for (Type genericType : Classes.genericSupertypes(subtype)) {
                    if (genericType.equals(supertype)) {
                        return Arrays.asList(((ParameterizedType) genericType).getActualTypeArguments());
                    }
                }
            }
        }

        return null;
    }
}
