package soulboundarmory.mixin.mixin;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.common.ForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerPlayNetworkHandler.class)
abstract class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @ModifyConstant(method = "onPlayerInteractEntity", constant = @Constant(doubleValue = 36))
    public double extendEntityReach(double thirtySix) {
        var reach = this.player.getAttributeValue(ForgeMod.REACH_DISTANCE.get());

        if (!this.player.isCreative()) {
            reach -= 0.5;
        }

        return reach * reach;
    }
}
