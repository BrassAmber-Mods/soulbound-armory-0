package user11681.soulboundarmory.asm.mixin.entity;

import net.minecraft.entity.LightningEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import user11681.soulboundarmory.asm.access.entity.LightningEntityAccess;

@Mixin(LightningEntity.class)
public abstract class LightningEntityMixin implements LightningEntityAccess {
    @Accessor
    @Override
    public abstract int getAmbientTick();

    @Accessor
    @Override
    public abstract void setAmbientTick(final int i);

    @Accessor
    @Override
    public abstract int getRemainingActions();

    @Accessor
    @Override
    public abstract void setRemainingActions(final int i);
}
