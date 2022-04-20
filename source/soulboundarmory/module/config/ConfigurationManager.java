package soulboundarmory.module.config;

import java.util.Map;
import java.util.stream.Collectors;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.auoeke.reflect.Classes;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public final class ConfigurationManager {
    private static final Map<Class<?>, ConfigurationInstance> entries = new Reference2ReferenceOpenHashMap<>();

    @SubscribeEvent
    public static void begin(FMLConstructModEvent event) {
        var mod = ModLoadingContext.get().getActiveContainer();
        var annotations = mod.getModInfo().getOwningFile().getFile().getScanResult().getAnnotations();

        for (var annotation : annotations.stream().collect(Collectors.groupingBy(ModFileScanData.AnnotationData::annotationType)).get(Type.getType(ConfigurationFile.class))) {
            var instance = new ConfigurationInstance(mod, Classes.load(annotation.clazz().getClassName()));
            entries.put(instance.type, instance);
            mod.registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> new ConfigGuiHandler.ConfigGuiFactory((client, parent) -> instance.screen(parent).asScreen()));
        }
    }

    public static ConfigurationInstance instance(Class<?> type) {
        return entries.get(type);
    }
}
