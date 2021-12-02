package soulboundarmory.mixin.mixin.entity;

import net.minecraft.entity.LightningEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import soulboundarmory.mixin.access.entity.LightningEntityAccess;

@Mixin(LightningEntity.class)
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
