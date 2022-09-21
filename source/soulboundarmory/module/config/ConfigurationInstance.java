package soulboundarmory.module.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;
import net.auoeke.lusr.Lusr;
import net.auoeke.lusr.element.LusrMap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.loading.FMLPaths;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.module.config.gui.ConfigurationScreen;
import soulboundarmory.util.Util;

public final class ConfigurationInstance extends Parent {
    private static final Timer deserializationTimer = new Timer(true);
    private static final Lusr lusr = new Lusr();

    public final ModContainer mod;
    public final Path path;
    public final Identifier background;

    private FileTime mtime = FileTime.from(Instant.EPOCH);
    private boolean desynced;

    public ConfigurationInstance(ModContainer mod, Class<?> type) {
        super(type, mod.getModId() + Util.value(type, (Name name) -> ':' + name.value(), ""), Util.value(type, Category::value, "main"));

        this.mod = mod;
        this.path = FMLPaths.CONFIGDIR.get().resolve(Util.value(type, Name::value, mod.getModId()) + ".lusr");
        var background = new Identifier(Util.value(type, Background::value, "block/andesite.png"));
        this.background = new Identifier(background.getNamespace(), "textures/" + background.getPath());

        if (this.deserialize()) {
            this.serialize();
        }

        deserializationTimer.schedule(new TimerTask() {
            @Override public void run() {
                if (ConfigurationInstance.this.desynced) {
                    ConfigurationInstance.this.desynced = false;
                    ConfigurationInstance.this.serialize();
                } else {
                    ConfigurationInstance.this.deserialize();
                }
            }
        }, 1000, 1000);
    }

    public ConfigurationScreen screen(Screen parent) {
        return new ConfigurationScreen(this, parent);
    }

    public boolean deserialize() {
        try {
            if (Files.exists(this.path)) {
                var mtime = Files.getLastModifiedTime(this.path);

                if (mtime.compareTo(this.mtime) > 0) {
                    try {
                        if (Lusr.parseResource(this.path) instanceof LusrMap configuration) {
                            this.deserialize(this, configuration);
                        }
                    } finally {
                        this.mtime = mtime;
                    }
                }

                return true;
            }

            return false;
        } catch (Throwable trouble) {
            SoulboundArmory.logger.error("Unable to deserialize configuration %s.".formatted(this.name), trouble);

            return false;
        }
    }

    public void serialize() {
        try {
            try (var output = Files.newBufferedWriter(this.path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
                lusr.serialize(output, this.toLusr(this));
            }
        } catch (Throwable trouble) {
            SoulboundArmory.logger.error("Unable to serialize configuration %s.".formatted(this.name), trouble);
        }
    }

    void desynced() {
        this.desynced = true;
    }

    private void deserialize(Parent parent, LusrMap map) {
        map.forEach((key, value) -> {
            var node = parent.children.get(key);

            if (value instanceof LusrMap submap) {
                if (node instanceof Group group) {
                    this.deserialize(group, submap);
                }
            } else if (node instanceof Property<?> property) {
                property.set(lusr.fromLusr(property.type, value));
            }
        });
    }

    private LusrMap toLusr(Parent parent) {
        var map = new LusrMap();

        for (var child : parent.children.values()) {
            if (child instanceof Group group) {
                map.put(group.name, this.toLusr(group));
            } else if (child instanceof Property<?> property) {
                map.put(property.name, lusr.toLusr(property.get()));
            }
        }

        return map;
    }
}
