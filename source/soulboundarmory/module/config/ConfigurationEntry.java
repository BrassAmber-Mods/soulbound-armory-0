package soulboundarmory.module.config;

import java.nio.file.Path;
import net.auoeke.reflect.Constructors;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.loading.FMLPaths;
import soulboundarmory.util.Util;

public final class ConfigurationEntry<C extends ConfigurationFile> extends ConfigurationParent {
    public final ModContainer mod;
    public final C instance;
    public final Path path;

    public ConfigurationEntry(ModContainer mod, Class<C> type) {
        super(type, mod.getModId() + Util.value(type, (Name name) -> ':' + name.value(), ""), Util.value(type, Category::value, "main"));

        this.mod = mod;
        this.instance = Constructors.instantiate(type);
        this.path = FMLPaths.CONFIGDIR.get().resolve(Util.value(type, Name::value, mod.getModId()) + ".eson");
    }
}
