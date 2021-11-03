package user11681.soulboundarmory.capability;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.NonNullConsumer;

public class CapabilityContainer<T> {
    private final Reference2ReferenceMap<ICapabilityProvider, T> capabilities = new Reference2ReferenceOpenHashMap<>();

    public final Capability<T> capability;

    public CapabilityContainer(Capability<T> capability) {
        this.capability = capability;
    }

    @SuppressWarnings("ConstantConditions")
    public T get(ICapabilityProvider provider) {
        return this.capabilities.computeIfAbsent(provider, provider2 -> provider2.getCapability(this.capability).orElse(null));
    }

    public boolean has(Entity entity) {
        return get(entity) != null;
    }

    public void ifPresent(ICapabilityProvider provider, NonNullConsumer<? super T> action) {
        T capability = this.get(provider);

        if (capability != null) {
            action.accept(capability);
        }
    }
}
