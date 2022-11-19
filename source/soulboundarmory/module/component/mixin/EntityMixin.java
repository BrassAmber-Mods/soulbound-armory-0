package soulboundarmory.module.component.mixin;

import java.util.Map;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soulboundarmory.module.component.ComponentRegistry;
import soulboundarmory.module.component.EntityComponent;
import soulboundarmory.module.component.EntityComponentKey;
import soulboundarmory.module.component.access.EntityAccess;
import soulboundarmory.util.Util;

@Mixin(Entity.class)
abstract class EntityMixin implements EntityAccess {
	@Unique
	private final Map<EntityComponentKey<?>, EntityComponent<?>> components = new Reference2ReferenceOpenHashMap<>();

	@Override
	public Map<EntityComponentKey<?>, EntityComponent<?>> soulboundarmory$components() {
		return this.components;
	}

	@Inject(method = "writeNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V"))
	public void serializeComponents(NbtCompound tag, CallbackInfoReturnable<NbtCompound> info) {
		var components = new NbtCompound();
		this.components.forEach((key, component) -> components.put(key.key, component.serialize()));
		tag.put(ComponentRegistry.componentKey, components);
	}

	@Inject(method = "readNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V"))
	public void deserializeComponents(NbtCompound tag, CallbackInfo info) {
		Util.ifPresent(tag, ComponentRegistry.componentKey, components -> this.components.forEach((key, component) -> Util.ifPresent(components, key.key, component::deserialize)));
	}

	@Inject(method = "copyFrom", at = @At("RETURN"))
	private void copyComponents(Entity original, CallbackInfo info) {
		this.components.forEach((key, component) -> {
			var originalComponent = key.of(original);

			if (originalComponent != null) {
				originalComponent.copy(Util.cast(component));
			}
		});
	}
}
