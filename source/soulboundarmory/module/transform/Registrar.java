package soulboundarmory.module.transform;

import java.lang.annotation.ElementType;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import com.google.common.base.Predicates;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.auoeke.reflect.Accessor;
import net.auoeke.reflect.Classes;
import net.auoeke.reflect.Flags;
import net.auoeke.reflect.Invoker;
import net.auoeke.reflect.Methods;
import net.auoeke.reflect.Reflect;
import net.minecraft.util.Identifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.util.Util;

@EventBusSubscriber(modid = SoulboundArmory.ID, bus = EventBusSubscriber.Bus.MOD)
public class Registrar {
    private static final Map<Class<?>, ForgeRegistry<?>> registryCache = new Reference2ReferenceOpenHashMap<>();
    private static final Map<Identifier, List<Registration>> registrations = new Object2ReferenceOpenHashMap<>();
    private static final List<Consumer<IForgeRegistry<?>>> registrationsByType = ReferenceArrayList.of();
    private static final Set<ModFileScanData.AnnotationData> fields = ReferenceOpenHashSet.of();

    @SubscribeEvent
    public static void register(FMLConstructModEvent construct) {
        Invoker.invoke(
            Invoker.bind(FMLJavaModLoadingContext.get().getModEventBus(), "addListener", void.class, EventPriority.class, Predicate.class, Class.class, Consumer.class),
            EventPriority.NORMAL, Predicates.alwaysTrue(), RegistryEvent.Register.class, (Consumer<RegistryEvent.Register>) Registrar::register
        );

        ModList.get().getMods().forEach(mod -> {
            mod.getOwningFile().getFile().getScanResult().getAnnotations().stream()
                .collect(Collectors.groupingBy(ModFileScanData.AnnotationData::annotationType))
                .forEach((type, annotations) -> {
                    if (type.equals(Type.getType(Register.class))) {
                        annotations.forEach(annotation -> {
                            var value = (List<String>) annotation.annotationData().get("value");
                            var identifier = value == null || annotation.targetType() == ElementType.TYPE ? null : switch (value.size()) {
                                case 1 -> Util.id(mod.getModId(), value.get(0));
                                default -> throw new IllegalArgumentException("value = %s; must be 1 element or absent".formatted(value));
                            };

                            var node = new ClassNode();
                            new ClassReader(Classes.classFile(annotation.clazz().getInternalName())).accept(node, annotation.targetType() == ElementType.METHOD ? 0 : ClassReader.SKIP_CODE);

                            if (annotation.targetType() == ElementType.METHOD) {
                                if (value == null || value.size() != 1) {
                                    throw new IllegalArgumentException("@Register on %s::%s must have 1 argument containing its identifier".formatted(node.name, annotation.memberName()));
                                }

                                processMethod(annotation, identifier, node);
                            } else if (value != null || annotation.targetType() == ElementType.FIELD) {
                                node.fields.stream().filter(field -> {
                                    if (annotation.targetType() == ElementType.FIELD) {
                                        if (field.name.equals(annotation.memberName())) {
                                            if (!Flags.isStatic(field.access) || !Flags.isFinal(field.access)) {
                                                throw new IllegalArgumentException("bad modifiers \"%s\" (%d) on %s.%s; must be static final".formatted(Flags.string(field.access), field.access, node.name, field.name));
                                            }
                                        } else {
                                            return false;
                                        }
                                    }

                                    return true;
                                }).forEach(field -> {
                                    if (value == null || annotation.targetType() == ElementType.FIELD) {
                                        registrationsByType.add(registry -> {
                                            if (registry.getRegistrySuperType().isAssignableFrom(Classes.load(Type.getType(field.desc).getClassName()))) {
                                                register(registry, identifier, node, field);
                                            }
                                        });
                                    } else for (var id : value) {
                                        var registration = new Registration(registry -> register(registry, identifier, node, field));
                                        registrations.computeIfAbsent(new Identifier(id), key -> ReferenceArrayList.of()).add(registration);
                                        registrations.computeIfAbsent(Util.id(id), key -> ReferenceArrayList.of()).add(registration);
                                    }
                                });
                            }
                        });
                    }
                });
        });
    }

    private static <T extends IForgeRegistryEntry<T>> ForgeRegistry<T> findRegistry(Class<T> type) {
        return (ForgeRegistry<T>) registryCache.computeIfAbsent(type, t -> RegistryManager.ACTIVE.registries.values().stream()
            .filter(entry -> entry.getRegistrySuperType().isAssignableFrom(t))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("no registry for " + t))
        );
    }

    private static void processMethod(ModFileScanData.AnnotationData annotation, Identifier id, ClassNode node) {
        FMLJavaModLoadingContext.get().getModEventBus().<NewRegistryEvent>addListener(event -> {
            var signature = annotation.memberName();
            var method = node.methods.stream()
                .filter(m -> m.name.equals(signature.substring(0, signature.indexOf('('))) && m.desc.equals(signature.substring(signature.indexOf('('))))
                .findFirst()
                .get();
            var field = (FieldNode) node.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, method.name + UUID.randomUUID(), Type.getDescriptor(IForgeRegistry.class), null, null);

            var instrumentation = Reflect.instrument().value();
            instrumentation.addTransformer(new SingleUseTransformer(instrumentation, node.name, (module, loader, name, type, domain, classFile) -> {
                method.access &= ~Flags.NATIVE;
                method.visitFieldInsn(Opcodes.GETSTATIC, node.name, field.name, field.desc);
                method.visitInsn(Opcodes.ARETURN);

                var writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                node.accept(writer);

                return writer.toByteArray();
            }));
            var declaringType = Classes.load(false, annotation.clazz().getClassName());

            if (Methods.of(declaringType, method.name).getGenericReturnType() instanceof ParameterizedType pt) {
                if (IForgeRegistry.class.isAssignableFrom((Class<?>) pt.getRawType())) {
                    var argument = pt.getActualTypeArguments()[0];

                    event.create(new RegistryBuilder<>().setName(id).setType(Util.cast(argument instanceof ParameterizedType p ? p.getRawType() : argument)), registry -> {
                        Accessor.putReference(declaringType, field.name, registry);
                    });
                } else {
                    throw new WrongMethodTypeException("@Register %s::%s return type must be assignable from IForgeRegistry".formatted(node.name, method.name));
                }
            } else {
                throw new WrongMethodTypeException("@Register %s::%s return type must be a specialized form of IForgeRegistry".formatted(node.name, method.name));
            }
        });
    }

    private static <T extends IForgeRegistryEntry<T>> void register(IForgeRegistry<T> registry, Identifier id, ClassNode owner, FieldNode field) {
        T entry = Accessor.getReference(Classes.load(owner.name.replace('/', '.')), field.name);

        if (id != null) {
            entry.setRegistryName(id);
        }

        if (entry.getRegistryName() == null) {
            throw new IllegalStateException("regsistry entry %s.%s does not have a name".formatted(owner.name, field.name));
        }

        registry.register(entry);
    }

    private static void register(RegistryEvent.Register<?> event) {
        var fields = registrations.get(event.getRegistry().getRegistryName());

        if (fields != null) {
            fields.forEach(registration -> {
                if (registration.action != null) {
                    registration.action.accept(event.getRegistry());
                    registration.action = null;
                }
            });
        }

        registrationsByType.forEach(register -> register.accept(event.getRegistry()));
    }

    private static class Registration {
        Consumer<IForgeRegistry<?>> action;

        Registration(Consumer<IForgeRegistry<?>> action) {
            this.action = action;
        }
    }
}
