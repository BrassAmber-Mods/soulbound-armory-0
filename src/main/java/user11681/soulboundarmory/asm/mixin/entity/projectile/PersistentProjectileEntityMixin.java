package user11681.soulboundarmory.asm.mixin.entity.projectile;

import net.minecraft.entity.projectile.AbstractArrowEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import user11681.soulboundarmory.asm.access.entity.AbstractArrowEntityAccess;

@Mixin(AbstractArrowEntity.class)
public abstract class AbstractArrowEntityMixin implements AbstractArrowEntityAccess {
    @Accessor
    @Override
    public abstract int getLife();
}
