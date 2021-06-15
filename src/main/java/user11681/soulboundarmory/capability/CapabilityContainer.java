package user11681.soulboundarmory.capability;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;

public class CapabilityContainer<T> {
    public final Capability<T> capability;

    public CapabilityContainer(Capability<T> capability) {
        this.capability = capability;
    }

    @SuppressWarnings("ConstantConditions")
    public T get(ICapabilityProvider provider) {
        return provider.getCapability(this.capability).orElse(null);
    }

    public LazyOptional<T> find(ICapabilityProvider provider) {
        return provider.getCapability(this.capability);
    }

    public boolean has(Entity entity) {
        return get(entity) != null;
    }

    public void ifPresent(ICapabilityProvider provider, NonNullConsumer<? super T> action) {
        this.find(provider).ifPresent(action);
    }
}
