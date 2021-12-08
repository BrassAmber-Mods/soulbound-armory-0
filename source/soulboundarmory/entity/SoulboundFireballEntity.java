package soulboundarmory.entity;

import net.minecraft.enchantment.EnchantmentHelper;
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
import soulboundarmory.SoulboundArmory;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.item.weapon.StaffComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.registry.Skills;
import soulboundarmory.serial.CompoundSerializable;

public class SoulboundFireballEntity extends SmallFireballEntity implements CompoundSerializable {
    public static final EntityType<SoulboundFireballEntity> type = EntityType.Builder
        .create((EntityType.EntityFactory<SoulboundFireballEntity>) SoulboundFireballEntity::new, SpawnGroup.MISC)
        .setDimensions(1, 1)
        .build(SoulboundArmory.id("fireball").toString());

    protected StaffComponent storage;
    protected int hitCount;
    protected int spell;

    public SoulboundFireballEntity(World world, PlayerEntity shooter, int spell) {
        this(world);

        this.updatePlayer();
        this.setPosition(shooter.getX(), shooter.getEyeY(), shooter.getZ());
        this.setRotation(shooter.pitch, shooter.yaw);
        this.setVelocity(shooter.getRotationVector().multiply(1.5, 1.5, 1.5));
    }

    public SoulboundFireballEntity(World world) {
        super(type, world);
    }

    protected void updatePlayer() {
        if (this.getEntity() != null) {
            this.storage = Components.weapon.of(this.getEntity()).item(ItemComponentType.staff);
        }
    }

    public SoulboundFireballEntity(EntityType<SoulboundFireballEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void onCollision(HitResult result) {
        if (!this.world.isClient && result.getType() == HitResult.Type.ENTITY && this.getEntity() instanceof PlayerEntity player) {
            var entity = ((EntityHitResult) result).getEntity();
            var fiery = this.isBurning();

            if (entity != null) {
                var data = Components.entityData.of(entity);
                var endermanacle = this.storage.skill(Skills.endermanacle);
                var invulnerable = entity.isFireImmune() || entity instanceof EndermanEntity;
                var canAttack = invulnerable && this.storage.hasSkill(Skills.vulnerability);
                var canBurn = (canAttack || !invulnerable) && fiery;
                var source = canAttack ? DamageSource.player(player) : DamageSource.fireball(this, player);

                if (endermanacle.learned()) {
                    data.blockTeleport(20 * (1 + endermanacle.level()));
                }

                if (canBurn) {
                    entity.setFireTicks(20);
                }

                if (entity.damage(source, (float) this.storage.attributeTotal(StatisticType.attackDamage))) {
                    EnchantmentHelper.onTargetDamaged(player, entity);

                    if (canBurn) {
                        entity.setFireTicks(100);
                    }

                    var penetration = this.storage.skill(Skills.penetration);

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
    public NbtCompound serializeNBT() {
        var tag = super.serializeNBT();
        tag.putInt("spell", this.spell);

        return tag;
    }

    @Override
    public void deserializeNBT(NbtCompound tag) {
        super.deserializeNBT(tag);

        this.spell = tag.getInt("spell");
    }
}
