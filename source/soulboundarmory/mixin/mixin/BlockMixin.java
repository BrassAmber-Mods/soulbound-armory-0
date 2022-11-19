package soulboundarmory.mixin.mixin;

import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.skill.Skills;

@Mixin(Block.class)
abstract class BlockMixin {
	@Unique
	private static PlayerEntity miner;

	/**
	 Store the miner in {@link #miner} for use in {@link #dropStackAtMiner} if it mined with a tool that has {@link Skills#enderPull}.
	 */
	@Inject(method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V",
	        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;"))
	private static void storeMinerForEnderPull(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool, CallbackInfo info) {
		ItemComponent.of(entity, tool).ifPresent(component -> {
			if (component.hasSkill(Skills.enderPull)) {
				miner = (PlayerEntity) entity;
			}
		});
	}

	/**
	 Clear {@link #miner} after the drops' locations have been changed.
	 */
	@Inject(method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V",
	        at = @At(value = "RETURN"))
	private static void removeMinerForEnderPull(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfo info) {
		miner = null;
	}

	@Inject(method = "dropStack(Lnet/minecraft/world/World;Ljava/util/function/Supplier;Lnet/minecraft/item/ItemStack;)V",
	        at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
	private static void insertEnderPullStackIntoInventory(World world, Supplier<ItemEntity> itemEntitySupplier, ItemStack stack, CallbackInfo info) {
		if (miner != null) {
			miner.getInventory().insertStack(stack);
		}
	}

	/**
	 This callback will be invoked if the item stack is still not empty from the previous callback.
	 */
	@Inject(method = "dropStack(Lnet/minecraft/world/World;Ljava/util/function/Supplier;Lnet/minecraft/item/ItemStack;)V",
	        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"),
	        locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void dropRemainingEnderPullStackAtMiner(World world, Supplier<ItemEntity> itemEntitySupplier, ItemStack stack, CallbackInfo info, ItemEntity drop) {
		if (miner != null) {
			drop.setPosition(miner.getX(), miner.getY(), miner.getZ());
		}
	}
}
