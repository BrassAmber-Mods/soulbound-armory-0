package soulboundarmory.module.config;

import java.util.List;
import java.util.Map;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.auoeke.reflect.Classes;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import org.objectweb.asm.Type;
import soulboundarmory.config.Configuration;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public final class ConfigurationManager {
    private static final Map<Class<?>, Entry<?>> entriesByType = new Reference2ReferenceOpenHashMap<>();
    private static final Map<ModContainer, List<Entry<?>>> entries = new Reference2ReferenceOpenHashMap<>();

    @SubscribeEvent
    public static void begin(FMLConstructModEvent event) {
        var mod = ModLoadingContext.get().getActiveContainer();
        var classes = mod.getModInfo().getOwningFile().getFile().getScanResult().getClasses();

        for (var type : classes) {
            if (type.interfaces().contains(Type.getType(ConfigurationFile.class))) {
                var entry = new Entry<>(mod, Classes.load(type.clazz().getClassName()));
                entriesByType.put(entry.type, entry);
                entries.computeIfAbsent(mod, m -> ReferenceArrayList.of()).add(entry);
                mod.registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> new ConfigGuiHandler.ConfigGuiFactory((client, parent) -> ConfigurationManager.entry(Configuration.class).screen(parent)));
            }
        }
    }

    public static Entry<?> entry(Class<?> type) {
        return entriesByType.get(type);
    }
}
