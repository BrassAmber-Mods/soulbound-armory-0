package soulboundarmory.component;

import net.minecraft.entity.Entity;

public abstract class EntityComponent<T extends Entity> implements Component {
    public final T entity;

    public EntityComponent(T entity) {
        this.entity = entity;
    }
}
