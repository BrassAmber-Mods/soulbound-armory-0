package soulboundarmory.mixin.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import soulboundarmory.component.Components;
import soulboundarmory.registry.Skills;

@Mixin(Block.class)
abstract class BlockMixin {
    @Unique
    private static Entity miner;

    /**
     Store the miner in {@link #miner} for use in {@link #teleportStack} if it mined with a tool that has {@link Skills#enderPull}.
     */
    @Inject(method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;"))
    private static void storeMinerForTeleportation(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfo info) {
        Components.tool.nullable(entity).flatMap(component -> component.component(stack)).ifPresent(tool -> {
            if (tool.hasSkill(Skills.enderPull)) {
                miner = entity;
            }
        });
    }

    /**
     Clear {@link #miner} because the drops have been teleported.
     */
    @Inject(method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V",
            at = @At(value = "RETURN"))
    private static void removeMinerForTeleportation(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfo info) {
        miner = null;
    }

    @Inject(method = "dropStack",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void teleportStack(World world, BlockPos pos, ItemStack stack, CallbackInfo info, float f, double d0, double d1, double d2, ItemEntity drop) {
        if (miner != null) {
            drop.teleport(miner.getX(), miner.getY(), miner.getZ());
            drop.resetPickupDelay();
        }
    }
}
