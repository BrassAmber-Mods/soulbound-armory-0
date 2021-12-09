package soulboundarmory.mixin.mixin.entity;

import net.minecraft.entity.LightningEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import soulboundarmory.mixin.access.LightningEntityAccess;

@Mixin(LightningEntity.class)
abstract class LightningEntityMixin implements LightningEntityAccess {
    @Accessor("ambientTick")
    @Override
    public abstract int life();

    @Accessor("ambientTick")
    @Override
    public abstract void life(int flashes);

    @Accessor("remainingActions")
    @Override
    public abstract int flashes();

    @Accessor("remainingActions")
    @Override
    public abstract void flashes(int flashes);
}
