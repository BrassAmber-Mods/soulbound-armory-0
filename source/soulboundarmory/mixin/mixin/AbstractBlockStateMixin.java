package soulboundarmory.mixin.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.skill.Skills;

@Mixin(AbstractBlock.AbstractBlockState.class)
abstract class AbstractBlockStateMixin {
	@Inject(method = "calcBlockBreakingDelta", at = @At("RETURN"), cancellable = true)
	private void circumspectionPreventInstantBreaking(PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> info) {
		ItemComponent.fromMainHand(player)
			.filter(component -> info.getReturnValueF() >= 1 && ((AbstractBlock.AbstractBlockState) (Object) this).getHardness(world, pos) != 0 && component.hasSkill(Skills.circumspection))
			.ifPresent(component -> info.setReturnValue(Math.nextDown(1)));
	}
}
