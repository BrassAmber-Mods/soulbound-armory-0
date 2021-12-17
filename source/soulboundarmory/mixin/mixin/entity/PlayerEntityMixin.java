package soulboundarmory.mixin.mixin.entity;

import net.auoeke.reflect.StackFrames;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.entity.SoulboundDaggerEntity;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin {
    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getMainHandStack()Lnet/minecraft/item/ItemStack;"))
    private ItemStack attackWithThrownDagger(PlayerEntity player) {
        return StackFrames.caller(2) == SoulboundDaggerEntity.class ? ItemComponentType.dagger.of(player).stack() : player.getMainHandStack();
    }
}
