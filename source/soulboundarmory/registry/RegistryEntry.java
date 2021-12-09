package soulboundarmory.registry;

import net.minecraft.util.Identifier;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import soulboundarmory.util.Util;

@SuppressWarnings("unchecked")
public abstract class RegistryEntry<T extends RegistryEntry<T>> implements IForgeRegistryEntry<T> {
    protected Identifier id;

    public RegistryEntry(Identifier id) {
        this.id = id;

        this.registry().register((T) this);
    }

    public RegistryEntry(String path) {
        this(Util.id(path));
    }

    public Identifier id() {
        return this.id;
    }

    public String string() {
        return this.id.toString();
    }

    public IForgeRegistry<T> registry() {
        return Util.registry(this.getClass());
    }

    @Override
    public Identifier getRegistryName() {
        return this.id;
    }

    @Override
    public T setRegistryName(Identifier id) {
        this.id = id;

        return (T) this;
    }

    @Override
    public Class<T> getRegistryType() {
        return this.registry().getRegistrySuperType();
    }
}
