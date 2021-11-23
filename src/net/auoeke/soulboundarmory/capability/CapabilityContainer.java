package net.auoeke.soulboundarmory.capability;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.Optional;
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
    public Optional<T> get(ICapabilityProvider provider) {
        return Optional.ofNullable(this.capabilities.computeIfAbsent(provider, provider2 -> provider2.getCapability(this.capability).orElse(null)));
    }

    public boolean has(Entity entity) {
        return this.get(entity).isPresent();
    }

    public void ifPresent(ICapabilityProvider provider, NonNullConsumer<? super T> action) {
        this.get(provider).ifPresent(action::accept);
    }
}
