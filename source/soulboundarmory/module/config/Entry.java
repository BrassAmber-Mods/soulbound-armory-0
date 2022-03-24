package soulboundarmory.module.config;

import java.nio.file.Path;
import net.auoeke.reflect.Constructors;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.loading.FMLPaths;
import soulboundarmory.util.Util;

public final class Entry<C extends ConfigurationFile> extends Parent {
    public final ModContainer mod;
    public final C instance;
    public final Path path;

    public Entry(ModContainer mod, Class<C> type) {
        super(type, mod.getModId() + Util.value(type, (Name name) -> ':' + name.value(), ""), Util.value(type, Category::value, "main"));

        this.mod = mod;
        this.instance = Constructors.instantiate(type);
        this.path = FMLPaths.CONFIGDIR.get().resolve(Util.value(type, Name::value, mod.getModId()) + ".eson");
    }
}
