package user11681.soulboundarmory.util;

import java.util.Arrays;
import java.util.HashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.Registry;
import user11681.soulboundarmory.SoulboundArmory;

public class Util {
    @SuppressWarnings("deprecation")
    public static MinecraftServer getServer() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT
            ? MinecraftClient.getInstance().getServer()
            : (MinecraftServer) FabricLoader.getInstance().getGameInstance();
    }

    public static <T> T nul() {
        return null;
    }

    @SafeVarargs
    public static <T> HashSet<T> hashSet(T... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }

    @SuppressWarnings("unchecked")
    public static <T> Registry<T> simpleRegistry(String path) {
        return (Registry<T>) FabricRegistryBuilder.createSimple(Object.class, SoulboundArmory.id(path)).buildAndRegister();
    }
}
