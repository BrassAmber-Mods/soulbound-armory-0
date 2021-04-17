package user11681.usersmanual.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;

public class ServerUtil {
    public static MinecraftServer getServer() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            //noinspection MethodCallSideOnly
            return MinecraftClient.getInstance().getServer();
        } else {
            return (MinecraftDedicatedServer) FabricLoader.getInstance().getGameInstance();
        }
    }
}
