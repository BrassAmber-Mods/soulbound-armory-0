package soulboundarmory;

import java.lang.annotation.ElementType;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.auoeke.reflect.Accessor;
import net.auoeke.reflect.Classes;
import net.auoeke.reflect.Flags;
import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import soulboundarmory.transform.Register;
import soulboundarmory.util.Util;

@Mod(SoulboundArmory.ID)
public final class SoulboundArmory {
    public static final String ID = "soulboundarmory";

    public static final String componentKey = Util.id("components").toString();
    public static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(Util.id("main"), () -> "0", "0"::equals, "0"::equals);
    @Register("critical_hit") public static final DefaultParticleType criticalHitParticleType = new DefaultParticleType(false);
    @Register("unlock") public static final DefaultParticleType unlockParticle = new DefaultParticleType(false);
    @Register("unlock") public static final SoundEvent unlockAnimationSound = new SoundEvent(Util.id("unlock"));

    private static final Type register = Type.getType(Register.class);

    public SoulboundArmory() {
        // ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, );
        // ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, (minecaft, parent) -> );

        var registryCache = new Reference2ReferenceOpenHashMap<Class<?>, ForgeRegistry<?>>();

        ModLoadingContext.get().getActiveContainer().getModInfo().getOwningFile().getFile().getScanResult().getAnnotations().stream().collect(Collectors.groupingBy(ModFileScanData.AnnotationData::annotationType)).forEach((type, annotations) -> {
            if (type.equals(register)) {
                annotations.forEach(annotation -> {
                    var value = (List<String>) annotation.annotationData().get("value");
                    var path = value == null || annotation.targetType() != ElementType.FIELD ? null : switch (value.size()) {
                        case 1 -> value.get(0);
                        default -> throw new IllegalArgumentException("value = %s; must be 1 element or absent".formatted(value));
                    };
                    var registries = value == null || annotation.targetType() != ElementType.TYPE ? null : value.stream()
                        .map(id -> Objects.requireNonNull(RegistryManager.ACTIVE.getRegistry(new Identifier(id)), () -> "no registry with identifier \"%s\"".formatted(id)))
                        .toList();

                    try {
                        var node = new ClassNode();
                        new ClassReader(annotation.clazz().getInternalName()).accept(node, ClassReader.SKIP_CODE);

                        node.fields.forEach(fieldNode -> {
                            if (annotation.targetType() == ElementType.FIELD) {
                                if (fieldNode.name.equals(annotation.memberName())) {
                                    if (!Flags.isStatic(fieldNode.access) || !Flags.isFinal(fieldNode.access)) {
                                        throw new IllegalArgumentException("bad modifiers \"%s\" (%d) on %s.%s; must be static final".formatted(Flags.string(fieldNode.access), fieldNode.access, node.name, fieldNode.name));
                                    }
                                } else {
                                    return;
                                }
                            }

                            processField(registryCache, Util.cast(registries), path, node, fieldNode);
                        });
                    } catch (Throwable trouble) {
                        throw Unsafe.throwException(trouble);
                    }
                });
            }
        });
    }

    private static <T extends IForgeRegistryEntry<T>> void processField(Map<Class<?>, ForgeRegistry<?>> registryCache, List<ForgeRegistry<T>> registries, String path, ClassNode owner, FieldNode field) {
        var type = Classes.<T>load(Type.getType(field.desc).getClassName());

        for (var registry : Objects.requireNonNullElseGet(registries, () -> List.of((ForgeRegistry<T>) findRegistry(registryCache, type)))) {
            if (registry.getRegistrySuperType().isAssignableFrom(type)) {
                FMLJavaModLoadingContext.get().getModEventBus().<RegistryEvent.Register<? extends T>, T>addGenericListener(registry.getRegistrySuperType(), event -> {
                    T entry = Accessor.getReference(Classes.load(owner.name.replace('/', '.')), field.name);

                    if (path != null) {
                        entry.setRegistryName(Util.id(path));
                    }

                    event.getRegistry().register(Util.cast(entry));
                });
            }
        }
    }

    private static ForgeRegistry<?> findRegistry(Map<Class<?>, ForgeRegistry<?>> registryCache, Class<?> type) {
        return registryCache.computeIfAbsent(type, t -> RegistryManager.ACTIVE.registries.values().stream().filter(entry -> entry.getRegistrySuperType().isAssignableFrom(t)).findFirst().orElseThrow(() -> new IllegalStateException("no registry for " + t)));
    }
}
