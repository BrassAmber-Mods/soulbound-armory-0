package soulboundarmory.module.config;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.auoeke.reflect.Accessor;
import net.auoeke.reflect.Classes;
import net.auoeke.reflect.Fields;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import org.objectweb.asm.Type;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public final class ConfigurationManager {
    private static final Map<ModContainer, List<Entry<?>>> entries = new Reference2ReferenceOpenHashMap<>();

    @SubscribeEvent
    public static void begin(FMLConstructModEvent event) {
        var mod = ModLoadingContext.get().getActiveContainer();
        var classes = mod.getModInfo().getOwningFile().getFile().getScanResult().getClasses();

        for (var type : classes) {
            if (type.interfaces().contains(Type.getType(ConfigurationFile.class))) {
                entries.computeIfAbsent(mod, m -> ReferenceArrayList.of()).add(new Entry<>(mod, Classes.load(type.clazz().getClassName())));
            }
        }
    }

    public static void paths(String mod, Class<?> holder) {
        var paths = new HashMap<Field, String>();
        Fields.of(holder).forEach(field -> paths(paths, field, String.format("config.%s.%s", mod, field.getName())));
    }

    public static void paths(Map<Field, String> paths, Field field, String path) {
        Fields.of(field.getType()).filter(child -> Accessor.get(child) != null).forEach(child -> paths(paths, child, path + "." + child.getName()));
    }
}
