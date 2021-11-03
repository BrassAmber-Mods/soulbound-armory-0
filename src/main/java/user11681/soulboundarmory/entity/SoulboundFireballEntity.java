package user11681.soulboundarmory.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
    public static final EntityType<SoulboundFireballEntity> type = EntityType.Builder
        .create((EntityType.EntityFactory<SoulboundFireballEntity>) SoulboundFireballEntity::new, SpawnGroup.MISC)
        .setDimensions(1, 1)
        .build(SoulboundArmory.id("fireball").toString());

    protected StaffStorage storage;
    protected int hitCount;
    protected int spell;

    public SoulboundFireballEntity(World world, PlayerEntity shooter, int spell) {
        this(world);

        this.updatePlayer();
        this.setPos(shooter.getX(), shooter.getEyeY(), shooter.getZ());
        this.setRotation(shooter.pitch, shooter.yaw);
        this.setVelocity(shooter.getRotationVector().multiply(1.5, 1.5, 1.5));
    }

    public SoulboundFireballEntity(World world) {
        super(type, world);
    }

    protected void updatePlayer() {
        if (this.getOwner() != null) {
            this.storage = Capabilities.weapon.get(this.getOwner()).storage(StorageType.staff);
        }
    }

    public SoulboundFireballEntity(EntityType<SoulboundFireballEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void onCollision(HitResult result) {
        if (!this.world.isClient && result.getType() == HitResult.Type.ENTITY && this.getOwner() instanceof PlayerEntity player) {
            Entity entity = ((EntityHitResult) result).getEntity();
            boolean fiery = this.isOnFire();

            if (entity != null) {
                EntityData data = Capabilities.entityData.get(entity);
                SkillContainer endermanacle = this.storage.skill(Skills.endermanacle);
                boolean invulnerable = entity.isFireImmune() || entity instanceof EndermanEntity;
                boolean canAttack = invulnerable && this.storage.hasSkill(Skills.vulnerability);
                boolean canBurn = (canAttack || !invulnerable) && fiery;
                DamageSource source = canAttack ? DamageSource.player(player) : DamageSource.fireball(this, player);

                if (endermanacle.learned()) {
                    data.blockTeleport(20 * (1 + endermanacle.level()));
                }

                if (canBurn) {
                    entity.setFireTicks(20);
                }

                if (entity.damage(source, (float) this.storage.attributeTotal(attackDamage))) {
                    EnchantmentHelper.onTargetDamaged(player, entity);

                    if (canBurn) {
                        entity.setFireTicks(100);
                    }

                     SkillContainer penetration = this.storage.skill(Skills.penetration);

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
    @OnlyIn(Dist.CLIENT)
    public ItemStack getItem() {
        return (this.isBurning() ? Items.FIRE_CHARGE : Items.ENDER_PEARL).getDefaultStack();
    }

    @Override
    protected boolean isBurning() {
        return this.spell == 1;
    }

    @Override
    public void deserializeNBT(NbtCompound tag) {
        super.deserializeNBT(tag);

        this.spell = tag.getInt("spell");
    }

    @Override
    public NbtCompound serializeNBT() {
        NbtCompound tag = super.serializeNBT();
        tag.putInt("spell", this.spell);

        return tag;
    }
}
