package soulboundarmory.module.transform;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceLists;
import net.auoeke.reflect.Accessor;
import net.auoeke.reflect.ClassDefiner;
import net.auoeke.reflect.Constructors;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import soulboundarmory.util.Util;

public class TransformerManager {
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

	private static synchronized boolean transform(ClassNode node) {
		return transformers.stream().map(transformer -> transformer.test(node)).reduce(false, Boolean::logicalOr);
	}

	static {
		// Load it here to prevent a class loading circle later.
		Util.nul();

		Accessor.<List<Object>>getReference(Accessor.<Object>getReference(MixinEnvironment.getCurrentEnvironment().getActiveTransformer(), "processor"), "coprocessors").add(Constructors.construct(
			ClassDefiner.make().loader(MixinEnvironment.class.getClassLoader()).classFile("org.spongepowered.asm.mixin.transformer.SoulboundArmoryCoprocessor").define(),
			(Predicate<ClassNode>) TransformerManager::transform
		));
	}
}
