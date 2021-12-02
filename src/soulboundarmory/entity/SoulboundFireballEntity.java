package soulboundarmory.entity;

import soulboundarmory.SoulboundArmory;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.StorageType;
import soulboundarmory.component.soulbound.item.weapon.StaffStorage;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.registry.Skills;
import soulboundarmory.serial.CompoundSerializable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SoulboundFireballEntity extends SmallFireballEntity implements CompoundSerializable {
    public static final EntityType<SoulboundFireballEntity> type = EntityType.Builder
        .create((EntityType.IFactory<SoulboundFireballEntity>) SoulboundFireballEntity::new, EntityClassification.MISC)
        .size(1, 1)
        .build(SoulboundArmory.id("fireball").toString());

    protected StaffStorage storage;
    protected int hitCount;
    protected int spell;

    public SoulboundFireballEntity(World world, PlayerEntity shooter, int spell) {
        this(world);

        this.updatePlayer();
        this.setPosition(shooter.getPosX(), shooter.getEyeHeight(), shooter.getPosZ());
        this.setRotation(shooter.rotationPitch, shooter.rotationYaw);
        this.setMotion(shooter.getLookVec().mul(1.5, 1.5, 1.5));
    }

    public SoulboundFireballEntity(World world) {
        super(type, world);
    }

    protected void updatePlayer() {
        if (this.getEntity() != null) {
            this.storage = Components.weapon.of(this.getEntity()).storage(StorageType.staff);
        }
    }

    public SoulboundFireballEntity(EntityType<SoulboundFireballEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote && result.getType() == RayTraceResult.Type.ENTITY && this.getEntity() instanceof PlayerEntity player) {
            var entity = ((EntityRayTraceResult) result).getEntity();
            var fiery = this.isBurning();

            if (entity != null) {
                var data = Components.entityData.of(entity);
                var endermanacle = this.storage.skill(Skills.endermanacle);
                var invulnerable = entity.isImmuneToFire() || entity instanceof EndermanEntity;
                var canAttack = invulnerable && this.storage.hasSkill(Skills.vulnerability);
                var canBurn = (canAttack || !invulnerable) && fiery;
                var source = canAttack ? DamageSource.causePlayerDamage(player) : DamageSource.func_233547_a_(this, player);

                if (endermanacle.learned()) {
                    data.blockTeleport(20 * (1 + endermanacle.level()));
                }

                if (canBurn) {
                    entity.forceFireTicks(20);
                }

                if (entity.attackEntityFrom(source, (float) this.storage.attributeTotal(StatisticType.attackDamage))) {
                    EnchantmentHelper.applyThornEnchantments(player, entity);

                    if (canBurn) {
                        entity.forceFireTicks(100);
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
        return (this.isFireballFiery() ? Items.FIRE_CHARGE : Items.ENDER_PEARL).getDefaultInstance();
    }

    @Override
    protected boolean isFireballFiery() {
        return this.spell == 1;
    }

    @Override
    public CompoundNBT serializeNBT() {
        var tag = super.serializeNBT();
        tag.putInt("spell", this.spell);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        super.deserializeNBT(tag);

        this.spell = tag.getInt("spell");
    }
}
