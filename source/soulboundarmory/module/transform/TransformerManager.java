package soulboundarmory.module.transform;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceLists;
import net.auoeke.reflect.Accessor;
import net.auoeke.reflect.ClassDefiner;
import net.auoeke.reflect.Constructors;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.mixin.EmptyMixinPlugin;

public class TransformerManager implements EmptyMixinPlugin {
	private static final List<Predicate<ClassNode>> transformers = ReferenceLists.synchronize(ReferenceArrayList.of());

	public static void addSingleUseTransformer(String name, Consumer<ClassNode> transformer) {
		transformers.add(new Predicate<>() {
			@Override public boolean test(ClassNode type) {
				if (type.name.equals(name)) {
					transformer.accept(type);
					transformers.remove(this);
					return true;
				}

				return false;
			}
		});
	}

	public static void addTransformer(Predicate<ClassNode> transformer) {
		transformers.add(transformer);
	}

	private static boolean transform(ClassNode node) {
		return transformers.stream().map(transformer -> transformer.test(node)).reduce(false, Boolean::logicalOr);
	}

	private static GeneratorAdapter adapter(ClassNode type, String name, String descriptor) {
		return new GeneratorAdapter(type.visitMethod(0, name, descriptor, null, null), 0, name, descriptor);
	}

	static {
		var pkg = "org/spongepowered/asm/mixin/transformer";
		var coprocessor = new ClassNode();
		coprocessor.visit(Opcodes.V17, 0, pkg + "/SoulboundArmoryCoprocessor", null, pkg + "/MixinCoprocessor", null);

		var transformer = (FieldNode) coprocessor.visitField(Opcodes.ACC_FINAL, "transformer", Predicate.class.descriptorString(), null, null);

		var generator = adapter(coprocessor, "<init>", "(Ljava/util/function/Predicate;)V");
		generator.loadThis();
		generator.visitMethodInsn(Opcodes.INVOKESPECIAL, coprocessor.superName, "<init>", "()V", false);
		generator.loadThis();
		generator.loadArg(0);
		generator.visitFieldInsn(Opcodes.PUTFIELD, coprocessor.name, transformer.name, transformer.desc);
		generator.returnValue();

		generator = adapter(coprocessor, "getName", "()" + String.class.descriptorString());
		generator.visitLdcInsn(SoulboundArmory.ID);
		generator.returnValue();

		generator = adapter(coprocessor, "postProcess", "(Ljava/lang/String;Lorg/objectweb/asm/tree/ClassNode;)Z");
		generator.loadThis();
		generator.visitFieldInsn(Opcodes.GETFIELD, coprocessor.name, transformer.name, transformer.desc);
		generator.loadArg(1);
		generator.visitMethodInsn(Opcodes.INVOKEINTERFACE, Type.getInternalName(Predicate.class), "test", "(Ljava/lang/Object;)Z", true);
		generator.returnValue();

		var writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		coprocessor.accept(writer);

		Accessor.<List<Object>>getReference(Accessor.<Object>getReference(MixinEnvironment.getCurrentEnvironment().getActiveTransformer(), "processor"), "coprocessors").add(Constructors.construct(
			ClassDefiner.make().loader(MixinEnvironment.class.getClassLoader()).classFile(writer.toByteArray()).define(),
			(Predicate<ClassNode>) TransformerManager::transform
		));
	}
}
