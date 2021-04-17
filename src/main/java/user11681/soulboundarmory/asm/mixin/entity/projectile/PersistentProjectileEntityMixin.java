package user11681.soulboundarmory.asm.mixin.entity.projectile;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import user11681.soulboundarmory.asm.access.entity.PersistentProjectileEntityAccess;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin implements PersistentProjectileEntityAccess {
    @Accessor
    @Override
    public abstract int getLife();
}
