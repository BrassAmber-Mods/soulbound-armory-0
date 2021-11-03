package net.auoeke.soulboundarmory.asm.mixin.entity;

import net.minecraft.entity.LightningEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.auoeke.soulboundarmory.asm.access.entity.LightningEntityAccess;

@Mixin(LightningEntity.class)
abstract class LightningEntityMixin implements LightningEntityAccess {
    @Accessor("ambientTick")
    @Override
    public abstract int ambientTick();

    @Accessor("ambientTick")
    @Override
    public abstract void ambientTick(int flashes);

    @Accessor("remainingActions")
    @Override
    public abstract int remainingActions();

    @Accessor("remainingActions")
    @Override
    public abstract void remainingActions(int flashes);
}
