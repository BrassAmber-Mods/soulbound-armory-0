package user11681.soulboundarmory.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import user11681.soulboundarmory.SoulboundArmory;

public class EntityTypes {
    public static final EntityType<SoulboundDaggerEntity> soulboundDagger = Registry
            .register(Registry.ENTITY_TYPE, new Identifier(SoulboundArmory.ID, "dagger"), FabricEntityTypeBuilder
                    .create(SpawnGroup.MISC, (EntityType.EntityFactory<SoulboundDaggerEntity>) SoulboundDaggerEntity::new)
                    .dimensions(EntityDimensions.fixed(1, 1)).build());
    public static final EntityType<SoulboundFireballEntity> soulboundFireball = Registry
            .register(Registry.ENTITY_TYPE, new Identifier(SoulboundArmory.ID, "fireball"), FabricEntityTypeBuilder
                    .create(SpawnGroup.MISC, (EntityType.EntityFactory<SoulboundFireballEntity>) SoulboundFireballEntity::new)
                    .dimensions(EntityDimensions.fixed(1, 1)).build());
}
