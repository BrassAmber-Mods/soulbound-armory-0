package soulboundarmory.mixin.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.ItemMarkerComponent;
import soulboundarmory.component.soulbound.item.tool.ToolComponent;
import soulboundarmory.skill.Skills;
import soulboundarmory.util.Util;

@Mixin(ItemStack.class)
abstract class ItemStackMixin {
    @Inject(method = "hasGlint", at = @At("HEAD"), cancellable = true)
    private void disableGlint(CallbackInfoReturnable<Boolean> info) {
        Components.marker.nullable((ItemStack) (Object) this)
            .map(ItemMarkerComponent::item)
            .map(component -> Components.config.of(component.player))
            .filter(component -> !component.glint)
            .ifPresent(component -> info.setReturnValue(false));
    }

    @Inject(method = "postMine", at = @At("RETURN"))
    private void addXPByEnderPull(World world, BlockState state, BlockPos pos, PlayerEntity miner, CallbackInfo info) {
        Components.marker.nullable(Util.cast(this)).ifPresent(marker -> marker.item().mined(state, pos));
    }

    @Inject(method = "use", at = @At("RETURN"))
    private void absorbTool(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info) {
        if (hand == Hand.MAIN_HAND && info.getReturnValue().getResult() == ActionResult.PASS) {
            Components.marker.nullable(Util.cast(this)).ifPresent(marker -> {
                if (marker.item() instanceof ToolComponent tool && tool.hasSkill(Skills.absorption)) {
                    tool.absorb();
                }
            });
        }
    }
}
