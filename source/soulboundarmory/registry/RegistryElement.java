package soulboundarmory.registry;

import net.minecraft.util.Identifier;
import net.minecraftforge.registries.ForgeRegistryEntry;

@SuppressWarnings("unchecked")
public abstract class RegistryElement<T extends RegistryElement<T>> extends ForgeRegistryEntry<T> {
    public Identifier id() {
        return this.getRegistryName();
    }

    public String string() {
        return this.id().toString();
    }
}
