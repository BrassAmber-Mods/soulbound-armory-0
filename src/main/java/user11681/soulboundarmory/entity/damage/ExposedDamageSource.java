package user11681.soulboundarmory.entity.damage;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import user11681.mirror.reflect.FieldWrapper;

public class ExposedDamageSource extends DamageSource {
    protected final DamageSource delegate;
    public final FieldWrapper<Boolean> unblockable;

    protected ExposedDamageSource(final DamageSource delegate) {
        super(delegate.name);

        this.delegate = delegate;
        this.unblockable = new FieldWrapper<>("isUnblockable", delegate);
    }

    public static ExposedDamageSource player(final PlayerEntity player) {
        return new ExposedDamageSource(DamageSource.player(player));
    }

    public ExposedDamageSource unblockable() {
        this.unblockable.set(true);

        return this;
    }
}
