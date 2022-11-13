package soulboundarmory.mixin;

import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public interface EmptyMixinPlugin extends IMixinConfigPlugin {
	@Override default void onLoad(String s) {}

	@Override default String getRefMapperConfig() {
		return null;
	}

	@Override default boolean shouldApplyMixin(String s, String s1) {
		return true;
	}

	@Override default void acceptTargets(Set<String> set, Set<String> set1) {}

	@Override default List<String> getMixins() {
		return null;
	}

	@Override default void preApply(String s, ClassNode node, String s1, IMixinInfo info) {}

	@Override default void postApply(String s, ClassNode node, String s1, IMixinInfo info) {}
}
