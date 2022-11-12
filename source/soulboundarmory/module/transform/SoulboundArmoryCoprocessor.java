package org.spongepowered.asm.mixin.transformer;

import java.util.function.Predicate;
import org.objectweb.asm.tree.ClassNode;
import soulboundarmory.SoulboundArmory;

public class SoulboundArmoryCoprocessor extends MixinCoprocessor {
	private final Predicate<ClassNode> transformer;

	SoulboundArmoryCoprocessor(Predicate<ClassNode> transformer) {
		this.transformer = transformer;
	}

	@Override String getName() {
		return SoulboundArmory.ID;
	}

	@Override boolean postProcess(String name, ClassNode node) {
		return this.transformer.test(node);
	}
}
