package soulboundarmory.mixin.mixin;

import java.util.Optional;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.skill.Skills;

@Mixin(ServerPlayerInteractionManager.class)
abstract class ServerPlayerInteractionManagerMixin {
	@Shadow
	public ServerPlayerEntity player;

	@ModifyVariable(method = "tryBreakBlock", at = @At(value = "LOAD", ordinal = 11), ordinal = 0)
	private BlockPos dropExperienceAtMinerWithEnderPull(BlockPos position) {
		return Optional.ofNullable(this.player)
			.flatMap(player -> ItemComponent.of(player, player.getMainHandStack()))
			.map(component -> component.hasSkill(Skills.enderPull) ? this.player.getBlockPos() : position)
			.orElse(position);
	}
}
