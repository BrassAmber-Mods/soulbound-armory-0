package user11681.soulboundarmory.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public record CapabilityContainer<T>(Capability<T> capability) {
    @SuppressWarnings("ConstantConditions")
    public T get(ICapabilityProvider provider) {
        return provider.getCapability(this.capability).orElse(null);
    }
}
