package user11681.soulboundarmory.asm.mixin.client;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(Mouse.class)
abstract class MouseMixin {
    @Inject(method = "onMouseScroll", at = @At("HEAD"))
}
