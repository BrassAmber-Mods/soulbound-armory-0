package soulboundarmory.lib.component.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soulboundarmory.lib.component.Component;
import soulboundarmory.lib.component.access.EntityAccess;

@Mixin(EntityType.class)
abstract class EntityTypeMixin {
    @Inject(method = "create(Lnet/minecraft/world/World;)Lnet/minecraft/entity/Entity;", at = @At("RETURN"))
    private void initializeComponents(CallbackInfoReturnable<Entity> info) {
        ((EntityAccess) info.getReturnValue()).soulboundarmory$components().values().forEach(Component::initialize);
    }
}
