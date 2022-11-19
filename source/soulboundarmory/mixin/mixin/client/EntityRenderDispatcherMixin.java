package soulboundarmory.mixin.mixin.client;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import soulboundarmory.component.Components;

@Mixin(EntityRenderDispatcher.class)
abstract class EntityRenderDispatcherMixin {
	@ModifyVariable(method = "render", at = @At(value = "HEAD"), ordinal = 1)
	float freezeTickDeltaWhileEntityFrozen(float tickDelta, Entity entity) {
		var component = Components.entityData.of(entity);
		return component.isFrozen() ? component.tickDelta : tickDelta;
	}
}
