package soulboundarmory.registry;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import soulboundarmory.util.Util;

@SuppressWarnings("unchecked")
public abstract class RegistryElement<T extends RegistryElement<T>> {
    protected Identifier id;

    public RegistryElement(Identifier id) {
        this.id = id;
        Registry.register(this.registry(), this.id, (T) this);
    }

    public RegistryElement(String path) {
        this(Util.id(path));
    }

    public Identifier id() {
        return this.id;
    }

    public String string() {
        return this.id.toString();
    }

    public Registry<? super T> registry() {
        return Util.registry((Class<T>) this.getClass());
    }
}
