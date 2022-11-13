package soulboundarmory.util;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.ObjIntConsumer;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.mclanguageprovider.MinecraftModContainer;
import org.apache.logging.log4j.util.TriConsumer;
import soulboundarmory.SoulboundArmory;

public class Util {
	public static final boolean isPhysicalClient = FMLEnvironment.dist == Dist.CLIENT;
	public static final IntSupplier zeroSupplier = () -> 0;

	private static final ThreadLocal<Boolean> isClient = ThreadLocal.withInitial(() -> isPhysicalClient && (RenderSystem.isOnRenderThread() || Thread.currentThread().getName().equals("Game thread")));
	private static final Map<Class<?>, Registry<?>> registries = new Reference2ReferenceOpenHashMap<>();

	public static void rotate(MatrixStack matrixes, Vec3f axis, float degrees) {
		matrixes.multiply(axis.getDegreesQuaternion(degrees));
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

	public static String namespace() {
		var mod = ModLoadingContext.get().getActiveContainer();
		return mod instanceof MinecraftModContainer ? SoulboundArmory.ID : mod.getNamespace();
	}

	public static Identifier id(String namespace, String path) {
		return path.contains(":") ? new Identifier(path) : new Identifier(namespace, path);
	}

	public static Identifier id(String path) {
		return id(namespace(), path);
	}

	public static void ifPresent(NbtCompound tag, String key, Consumer<NbtCompound> action) {
		var child = (NbtCompound) tag.get(key);

		if (child != null) {
			action.accept(child);
		}
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
}
