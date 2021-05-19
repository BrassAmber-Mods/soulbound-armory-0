package user11681.soulboundarmory.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import user11681.soulboundarmory.SoulboundArmory;

public class Util {
    public static MinecraftServer getServer() {
        return FMLEnvironment.dist == Dist.CLIENT
            ? Minecraft.getInstance().level.getServer()
            : (MinecraftServer) ;
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

    public static <T extends IForgeRegistryEntry<T>> IForgeRegistry<T> registry(String path) {
        return new RegistryBuilder<T>().setName(SoulboundArmory.id(path)).create();
    }
}
