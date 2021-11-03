package net.auoeke.soulboundarmory.capability;

import net.minecraft.entity.Entity;

public class EntityCapability<T extends Entity> {
    public final T entity;

    public EntityCapability(T entity) {
        this.entity = entity;
    }
}
