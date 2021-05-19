package user11681.soulboundarmory.asm.mixin.entity;

import net.minecraft.entity.effect.LightningBoltEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import user11681.soulboundarmory.asm.access.entity.LightningEntityAccess;

@Mixin(LightningBoltEntity.class)
abstract class LightningEntityMixin implements LightningEntityAccess {
    @Accessor("life")
    @Override
    public abstract int life();

    @Accessor("life")
    @Override
    public abstract void life(int flashes);

    @Accessor("flashes")
    @Override
    public abstract int flashes();

    @Accessor("flashes")
    @Override
    public abstract void flashes(int flashes);
}
