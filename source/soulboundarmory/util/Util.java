package soulboundarmory.util;

import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Lifecycle;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.INameMappingService;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.mclanguageprovider.MinecraftModContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.util.TriConsumer;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.registry.RegistryElement;
import soulboundarmory.registry.SimplerRegistry;

public class Util extends net.minecraft.util.Util {
    public static final boolean isPhysicalClient = FMLEnvironment.dist == Dist.CLIENT;
    public static final String formattingValueField = map("field_1072", "$VALUES");

    private static final ThreadLocal<Boolean> isClient = ThreadLocal.withInitial(() -> isPhysicalClient && (RenderSystem.isOnRenderThread() || Thread.currentThread().getName().equals("Game thread")));
    private static final Map<Class<?>, Registry<?>> registries = new Reference2ReferenceOpenHashMap<>();
    private static BiFunction<INameMappingService.Domain, String, String> mapper;

    public static void rotate(MatrixStack matrixes, Vec3f axis, float degrees) {
        matrixes.multiply(axis.getDegreesQuaternion(degrees));
    }

    public static <T> Optional<T> or(Optional<T> optional, Supplier<? extends T> alternative) {
        return optional.or(() -> Optional.ofNullable(alternative.get()));
    }

    public static <T> T or(T t, Supplier<? extends T> alternative) {
        return t == null ? alternative.get() : t;
    }

    public static <T> boolean ifPresent(Optional<T> optional, Consumer<? super T> action) {
        if (optional.isPresent()) {
            action.accept(optional.get());

            return true;
        }

        return false;
    }

    public static <T> Iterable<T> iterate(Stream<T> stream) {
        return stream::iterator;
    }

    public static <T> T[] fill(T[] array, Supplier<T> element) {
        for (var index = 0; index < array.length; ++index) {
            array[index] = element.get();
        }

        return array;
    }

    public static <T> T[][] fill2(T[][] array, Supplier<T> element) {
        for (var subarray : array) {
            fill(subarray, element);
        }
        
        return array;
    }
    
    public static <B> B nul(Object... vacuum) {
        return null;
    }

    public static <T> T cast(Object object) {
        return (T) object;
    }

    public static String capitalize(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    public static <T> T[] array(T... elements) {
        return elements;
    }

    public static <T> List<T> list(T... elements) {
        return ReferenceArrayList.wrap(elements);
    }

    public static <K, V> Map<K, V> map(Map.Entry<K, V>... entries) {
        var map = new Reference2ReferenceLinkedOpenHashMap<K, V>();
        Stream.of(entries).forEach(entry -> map.put(entry.getKey(), entry.getValue()));

        return map;
    }

    public static <K, V, T extends Map<K, V>> T add(T map, Map.Entry<K, V> entry) {
        map.put(entry.getKey(), entry.getValue());
        return map;
    }

    public static boolean isClient() {
        return isClient.get();
    }

    public static boolean isServer() {
        return !isClient();
    }

    public static MinecraftServer server() {
        return (MinecraftServer) LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER);
    }

    public static boolean containsIgnoreCase(String string, String substring) {
        return Pattern.compile(substring, Pattern.CASE_INSENSITIVE | Pattern.LITERAL | Pattern.UNICODE_CASE).matcher(string).find();
    }

    public static boolean contains(Object target, Object... items) {
        return Arrays.asList(items).contains(target);
    }

    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <T> Set<T> hashSet(T... elements) {
        return new ObjectOpenHashSet<>(elements);
    }

    public static <T> Class<T> componentType(T... array) {
        return (Class<T>) array.getClass().getComponentType();
    }

    public static <A> A[] add(A[] array, A element) {
        var union = Arrays.copyOf(array, array.length + 1);
        union[array.length] = element;

        return union;
    }

    public static <A> A[] add(A element, A[] array) {
        var union = (A[]) Array.newInstance(Util.componentType(array), array.length + 1);
        System.arraycopy(array, 0, union, 1, array.length);
        union[0] = element;

        return union;
    }

    public static String namespace() {
        var mod = ModLoadingContext.get().getActiveContainer();
        return mod instanceof MinecraftModContainer ? SoulboundArmory.ID : mod.getNamespace();
    }

    public static Identifier id(String path) {
        return new Identifier(namespace(), path);
    }

    public static void ifPresent(NbtCompound tag, String key, Consumer<NbtCompound> action) {
        var child = (NbtCompound) tag.get(key);

        if (child != null) {
            action.accept(child);
        }
    }

    public static <T extends RegistryElement<T>> SimplerRegistry<T> newRegistry(String path, T... dummy) {
        var key = RegistryKey.<T>ofRegistry(id(path));
        var registry = Registry.register(cast(Registry.REGISTRIES), key, new SimplerRegistry<T>(key, Lifecycle.experimental(), null));
        registries.put(componentType(dummy), registry);

        return registry;
    }

    public static <T> Registry<? super T> registry(Class<T> type) {
        return type == null ? null : (Registry<? super T>) registries.computeIfAbsent(type, __ -> registry(type.getSuperclass()));
    }

    public static <K, V> void enumerate(Map<K, V> map, TriConsumer<K, V, Integer> action) {
        var count = 0;

        for (var entry : map.entrySet()) {
            action.accept(entry.getKey(), entry.getValue(), count++);
        }
    }

    public static <T> void enumerate(Iterable<T> iterable, ObjIntConsumer<T> action) {
        var count = 0;

        for (var element : iterable) {
            action.accept(element, count++);
        }
    }

    public static <T> void each(Iterable<? extends Reference<? extends T>> iterable, Consumer<? super T> action) {
        var iterator = iterable.iterator();

        while (iterator.hasNext()) {
            var reference = iterator.next();

            if (reference == null) {
                LogManager.getLogger("soulbound-armory").error("🤨 Something's fishy.");
            } else if (!reference.refersTo(null)) {
                action.accept(reference.get());

                continue;
            }

            try {
                iterator.remove();
            } catch (IndexOutOfBoundsException __)  {
                LogManager.getLogger("soulbound-armory").error("Something is very fishy.");
            }
        }
    }

    public static String map(String development, String production) {
        return FMLEnvironment.production ? production : development;
    }

    public static String mapClass(String production) {
        return map(INameMappingService.Domain.CLASS, production);
    }

    public static String mapMethod(int production) {
        return map(INameMappingService.Domain.METHOD, "m_%d_".formatted(production));
    }

    public static String mapField(int production) {
        return map(INameMappingService.Domain.FIELD, "f_%d_".formatted(production));
    }

    private static String map(INameMappingService.Domain domain, String production) {
        if (FMLEnvironment.production) {
            return production;
        }

        if (mapper == null) {
            mapper = Launcher.INSTANCE.environment().findNameMapping("srg").get();
        }

        return mapper.apply(domain, production);
    }
}
