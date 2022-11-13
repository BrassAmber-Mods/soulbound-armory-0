package soulboundarmory.module.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;
import net.auoeke.sp.StructuredProperties;
import net.auoeke.sp.element.SpMap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.loading.FMLPaths;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.module.config.gui.ConfigurationScreen;
import soulboundarmory.util.Util2;

public final class ConfigurationInstance extends Parent {
	private static final Timer deserializationTimer = new Timer(true);
	private static final StructuredProperties sp = new StructuredProperties();

	public final ModContainer mod;
	public final Path path;
	public final Identifier background;

	private FileTime mtime = FileTime.from(Instant.EPOCH);
	private boolean desynced;

	public ConfigurationInstance(ModContainer mod, Class<?> type) {
		super(type, mod.getModId() + Util2.value(type, (Name name) -> ':' + name.value(), ""), Util2.value(type, Category::value, "main"));

		this.mod = mod;
		this.path = FMLPaths.CONFIGDIR.get().resolve(Util2.value(type, Name::value, mod.getModId()) + ".str");
		var background = new Identifier(Util2.value(type, Background::value, "block/andesite.png"));
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

	public synchronized boolean deserialize() {
		try {
			if (Files.exists(this.path)) {
				var mtime = Files.getLastModifiedTime(this.path);

				if (mtime.compareTo(this.mtime) > 0) {
					try {
						if (StructuredProperties.parseResource(this.path) instanceof SpMap configuration) {
							this.deserialize(this, configuration);
						}
					} finally {
						this.mtime = mtime;
					}
				}
			}

			return true;
		} catch (Throwable trouble) {
			SoulboundArmory.logger.error("Unable to deserialize configuration %s.".formatted(this.name), trouble);

			return false;
		}
	}

	public synchronized void serialize() {
		try {
			try (var output = Files.newBufferedWriter(this.path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
				sp.serialize(output, this.toSp(this));
				this.mtime = FileTime.from(Instant.now());
			}
		} catch (Throwable trouble) {
			SoulboundArmory.logger.error("Unable to serialize configuration %s.".formatted(this.name), trouble);
		}
	}

	void desynced() {
		this.desynced = true;
	}

	private void deserialize(Parent parent, SpMap map) {
		map.forEach((key, value) -> {
			var node = parent.children.get(key);

			if (value instanceof SpMap submap) {
				if (node instanceof Group group) {
					this.deserialize(group, submap);
				}
			} else if (node instanceof Property<?> property) {
				property.set(sp.fromSp(property.type, value));
			}
		});
	}

	private SpMap toSp(Parent parent) {
		var map = new SpMap();

		for (var child : parent.children.values()) {
			if (child instanceof Group group) {
				map.put(group.name, this.toSp(group));
			} else if (child instanceof Property<?> property) {
				map.put(property.name, sp.toSp(property.get()));
			}
		}

		return map;
	}
}
