package soulboundarmory.module.transform;

import java.lang.invoke.WrongMethodTypeException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import com.google.common.base.Predicates;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.auoeke.reflect.Accessor;
import net.auoeke.reflect.Classes;
import net.auoeke.reflect.Fields;
import net.auoeke.reflect.Flags;
import net.auoeke.reflect.Invoker;
import net.auoeke.reflect.Methods;
import net.auoeke.reflect.Pointer;
import net.minecraft.util.Identifier;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryBuilder;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.registry.Identifiable;
import soulboundarmory.util.Util;
import soulboundarmory.util.Util2;

@EventBusSubscriber(modid = SoulboundArmory.ID, bus = EventBusSubscriber.Bus.MOD)
public class Registrar {
	private static final Map<Identifier, List<Consumer<RegisterEvent>>> registrations = new Object2ReferenceOpenHashMap<>();
	private static final Set<ModFileScanData.AnnotationData> fields = ReferenceOpenHashSet.of();

	@SubscribeEvent
	public static void register(FMLConstructModEvent construct) {
		Invoker.invoke(
			Invoker.bind(FMLJavaModLoadingContext.get().getModEventBus(), "addListener", void.class, EventPriority.class, Predicate.class, Class.class, Consumer.class),
			EventPriority.NORMAL, Predicates.alwaysTrue(), RegisterEvent.class, (Consumer<RegisterEvent>) Registrar::register
		);

		ModList.get().getMods().forEach(mod -> {
			var annotationMap = mod.getOwningFile().getFile().getScanResult().getAnnotations().stream().collect(Collectors.groupingBy(ModFileScanData.AnnotationData::annotationType));
			Optional.ofNullable(annotationMap.get(Type.getType(Registry.class))).ifPresent(annotations -> annotations.forEach(annotation -> processMethod(annotation, mod)));

			var registerAlls = new HashMap<Type, RegistrationInfo>();

			Optional.ofNullable(annotationMap.get(Type.getType(RegisterAll.class))).ifPresent(annotations -> annotations.forEach(annotation -> {
				var elements = annotation.annotationData();
				registerAlls.put(annotation.clazz(), new RegistrationInfo((String) elements.get("registry"), (Type) elements.get("type")));
			}));

			var idPointer = Pointer.of(Identifiable.class, "id");

			Optional.ofNullable(annotationMap.get(Type.getType(Register.class))).ifPresent(annotations -> annotations.forEach(annotation -> {
				var value = (String) annotation.annotationData().get("value");
				var registry = (String) annotation.annotationData().get("registry");
				var checkType = registry == null;
				var ownerName = annotation.clazz();
				var registerAll = registerAlls.get(ownerName);

				if (checkType) {
					if (registerAll == null) {
						throw new IllegalArgumentException("registry not specified for @Register %s.%s".formatted(ownerName.getInternalName(), annotation.memberName()));
					}

					registry = registerAll.registry;
				}

				Consumer<RegisterEvent> register = event -> {
					var owner = Class.forName(ownerName.getClassName());
					var field = Fields.of(owner, annotation.memberName());

					if (Flags.not(field, Flags.STATIC | Flags.FINAL)) {
						throw new IllegalArgumentException("bad modifiers \"%s\" (%d) on %s.%s; must be static final".formatted(Flags.string(field.getModifiers()), field.getModifiers(), ownerName.getInternalName(), field.getName()));
					}

					if (!checkType || Class.forName(registerAll.type.getClassName()).isAssignableFrom(field.getType())) {
						var identifier = Util.id(mod.getModId(), value);
						SoulboundArmory.logger.info("Registering {}.{} to \"{}\" as \"{}\".", ownerName.getInternalName(), field.getName(), event.getRegistryKey().getValue(), identifier);
						var object = Accessor.getReference(owner, field.getName());
						event.register(Util2.cast(event.getRegistryKey()), helper -> helper.register(identifier, object));

						if (object instanceof Identifiable) {
							idPointer.put(object, identifier);
						}
					}
				};

				registrations.computeIfAbsent(new Identifier(registry), id -> ReferenceArrayList.of()).add(register);
				registrations.computeIfAbsent(Util.id(mod.getModId(), registry), id -> ReferenceArrayList.of()).add(register);
			}));
		});
	}

	private static void processMethod(ModFileScanData.AnnotationData annotation, IModInfo mod) {
		var id = Util.id(mod.getModId(), (String) annotation.annotationData().get("value"));
		var type = annotation.clazz().getInternalName();

		FMLJavaModLoadingContext.get().getModEventBus().<NewRegistryEvent>addListener(event -> {
			var signature = annotation.memberName();
			var name = signature.substring(0, signature.indexOf('('));
			var fieldName = name + UUID.randomUUID();
			var fieldDescriptor = Type.getDescriptor(IForgeRegistry.class);

			TransformerManager.addSingleUseTransformer(type, node -> {
				var field = (FieldNode) node.visitField(Flags.PRIVATE | Flags.STATIC | Flags.FINAL, fieldName, fieldDescriptor, null, null);
				var method = node.methods.stream()
					.filter(m -> m.name.equals(name) && m.desc.equals(signature.substring(signature.indexOf('('))))
					.findFirst()
					.get();

				method.access &= ~Flags.NATIVE;
				method.visitFieldInsn(Opcodes.GETSTATIC, node.name, field.name, field.desc);
				method.visitInsn(Opcodes.ARETURN);
			});

			var declaringType = Classes.load(false, annotation.clazz().getClassName());
			var method = Methods.firstOf(declaringType, name);

			if (!Flags.isStatic(method)) {
				throw new WrongMethodTypeException("@Register %s::%s is not static".formatted(type, name));
			}

			if (IForgeRegistry.class.isAssignableFrom(method.getReturnType())) {
				event.create(new RegistryBuilder<>().setName(id), registry -> Accessor.putReference(declaringType, fieldName, registry));
			} else {
				throw new WrongMethodTypeException("@Register %s::%s return type is not assignable from IForgeRegistry".formatted(type, name));
			}
		});
	}

	private static synchronized void register(RegisterEvent event) {
		var registrations = Registrar.registrations.get(event.getRegistryKey().getValue());

		if (registrations != null) {
			for (var iterator = registrations.iterator(); iterator.hasNext(); iterator.remove()) {
				iterator.next().accept(event);
			}
		}
	}

	private record RegistrationInfo(String registry, Type type) {}
}
