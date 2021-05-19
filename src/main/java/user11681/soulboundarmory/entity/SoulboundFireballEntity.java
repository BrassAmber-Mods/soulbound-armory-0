package user11681.soulboundarmory.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.capability.entity.EntityData;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.capability.soulbound.item.weapon.StaffStorage;
import user11681.soulboundarmory.registry.Skills;
import user11681.soulboundarmory.serial.CompoundSerializable;
import user11681.soulboundarmory.skill.SkillContainer;

import static user11681.soulboundarmory.capability.statistics.StatisticType.attackDamage;

public class SoulboundFireballEntity extends SmallFireballEntity implements CompoundSerializable {
    public static final EntityType<SoulboundFireballEntity> type = Registry
        .register(ForgeRegistries.ENTITIES, new ResourceLocation(SoulboundArmory.ID, "fireball"), FabricEntityTypeBuilder
            .create(CreatureAttribute.UNDEFINED, (EntityType.IFactory<SoulboundFireballEntity>) SoulboundFireballEntity::new)
            .dimensions(EntityDimensions.fixed(1, 1)).build());

    protected StaffStorage storage;
    protected int hitCount;
    protected int spell;

    public SoulboundFireballEntity(World world) {
        super(type, world);
    }

    public SoulboundFireballEntity(World world, PlayerEntity shooter, int spell) {
        this(world);

        this.updatePlayer();
        this.setPos(shooter.getX(), shooter.getEyeY(), shooter.getZ());
        this.setRot(shooter.xRot, shooter.yRot);
        this.setDeltaMovement(shooter.getLookAngle().multiply(1.5, 1.5, 1.5));
    }

    public SoulboundFireballEntity(EntityType<SoulboundFireballEntity> type, World world) {
        super(type, world);
    }

    protected void updatePlayer() {
        if (this.getOwner() != null) {
            this.storage = Capabilities.weapon.get(this.getOwner()).storage(StorageType.staff);
        }
    }

    @Override
    protected void onHit(RayTraceResult result) {
        if (!this.level.isClientSide && result.getType() == RayTraceResult.Type.ENTITY && this.getOwner() instanceof PlayerEntity player) {
            Entity entity = ((EntityRayTraceResult) result).getEntity();
            boolean fiery = this.isOnFire();

            if (entity != null) {
                EntityData data = Capabilities.entityData.get(entity);
                SkillContainer endermanacle = storage.skill(Skills.endermanacle);
                boolean invulnerable = entity.fireImmune() || entity instanceof EndermanEntity;
                boolean canAttack = invulnerable && this.storage.hasSkill(Skills.vulnerability);
                boolean canBurn = (canAttack || !invulnerable) && fiery;
                DamageSource source = canAttack ? DamageSource.playerAttack(player) : DamageSource.fireball(this, player);

                if (endermanacle.learned()) {
                    data.blockTeleport(20 * (1 + endermanacle.level()));
                }

                if (canBurn) {
                    entity.setRemainingFireTicks(20);
                }

                if (entity.hurt(source, (float) storage.getAttributeTotal(attackDamage))) {
                    EnchantmentHelper.doPostDamageEffects(player, entity);

                    if (canBurn) {
                        entity.setRemainingFireTicks(100);
                    }

                    final SkillContainer penetration = storage.skill(Skills.penetration);

                    if (penetration.learned() && this.hitCount < penetration.level() + 1) {
                        this.hitCount++;
                    } else {
                        this.remove();
                    }
                }
            } else {
                this.remove();
            }
        }
    }

    @Override
    protected boolean shouldBurn() {
        return this.spell == 1;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack getItem() {
        return (this.shouldBurn() ? Items.FIRE_CHARGE : Items.ENDER_PEARL).getDefaultInstance();
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        super.deserializeNBT(tag);

        this.spell = tag.getInt("spell");
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = super.serializeNBT();
        tag.putInt("spell", this.spell);

        return tag;
    }
}
