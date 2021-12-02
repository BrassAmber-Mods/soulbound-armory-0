package soulboundarmory.entity;

import java.util.UUID;
import soulboundarmory.mixin.access.entity.LightningEntityAccess;
import soulboundarmory.component.soulbound.item.weapon.SwordStorage;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.serial.CompoundSerializable;
import net.minecraft.block.PortalSize;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SoulboundLightningEntity extends LightningBoltEntity implements LightningEntityAccess, CompoundSerializable {
    protected UUID caster;

    public SoulboundLightningEntity(World world, double x, double y, double z, UUID caster) {
        super(EntityType.LIGHTNING_BOLT, world);

        this.setEffectOnly(true);
        this.caster = caster;
        this.setPosition(x, y, z);

        PortalSize.func_242964_a(world, this.getPosition(), null).ifPresent(PortalSize::placePortalBlocks);
    }

    public SoulboundLightningEntity(World world, Vector3d pos, UUID caster) {
        this(world, pos.x, pos.y, pos.z, caster);
    }

    @Override
    public void tick() {
        if (!this.world.isRemote) {
            this.setFlag(6, this.isGlowing());
        }

        this.baseTick();

        if (this.life() == 2) {
            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 15, 0.8F + this.rand.nextFloat() * 0.2F);
            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2, 0.5F + this.rand.nextFloat() * 0.2F);
        }

        this.life(this.life() - 1);

        if (this.life() < 0) {
            if (this.flashes() == 0) {
                this.remove();
            } else if (this.life() < -this.rand.nextInt(10)) {
                this.flashes(this.flashes() - 1);
                this.life(1);
                this.boltVertex = this.rand.nextLong();
            }
        }

        if (this.life() >= 0) {
            if (this.world.isRemote) {
                this.world.setTimeLightningFlash(2);
            } else {
                var radius = 3D;

                for (var entity : this.world.getEntitiesWithinAABBExcludingEntity(this, new AxisAlignedBB(this.getPosX() - radius, this.getPosY() - radius, this.getPosZ() - radius, this.getPosX() + radius, this.getPosY() + 6 + radius, this.getPosZ() + radius))) {
                    var caster = this.caster();
                    var attackDamage = caster instanceof PlayerEntity
                            ? (float) SwordStorage.get(caster).attributeTotal(StatisticType.attackDamage)
                            : 5;

                    if (entity != caster && entity instanceof LivingEntity) {
                        entity.func_241841_a((ServerWorld) this.world, this);
                        entity.forceFireTicks(1);

                        if (!entity.isBurning()) {
                            this.forceFireTicks(160);
                        }

                        if (caster instanceof PlayerEntity) {
                            var target = (LivingEntity) entity;
                            var itemStack = caster.getHeldItemMainhand();
                            var damageSource = DamageSource.causeExplosionDamage(caster);
                            var attackDamageModifier = EnchantmentHelper.getModifierForCreature(itemStack, target.getCreatureAttribute());
                            var burnTime = 0;

                            if (attackDamage > 0 || attackDamageModifier > 0) {
                                var knockbackModifier = EnchantmentHelper.getKnockbackModifier(caster);
                                var initialHealth = target.getHealth();

                                burnTime += 4 * EnchantmentHelper.getFireAspectModifier(caster);

                                if (!caster.isBurning()) {
                                    burnTime += 5;
                                }

                                if (burnTime > 0 && !entity.isBurning()) {
                                    entity.forceFireTicks(20);
                                }

                                if (entity.attackEntityFrom(damageSource, attackDamage)) {
                                    var player = (PlayerEntity) caster;

                                    if (knockbackModifier > 0) {
                                        target.applyKnockback(knockbackModifier * 0.5F, MathHelper.sin(caster.rotationYaw * 0.017453292F), -MathHelper.cos(caster.rotationYaw * 0.017453292F));
                                    }

                                    if (attackDamageModifier > 0) {
                                        player.onCriticalHit(entity);
                                    }

                                    target.setRevengeTarget(caster);

                                    EnchantmentHelper.applyThornEnchantments(caster, target);

                                    var damageDealt = initialHealth - target.getHealth();

                                    if (burnTime > 0) {
                                        entity.setFire(burnTime);
                                    }

                                    if (caster.world instanceof ServerWorld && damageDealt > 2) {
                                        var particles = (int) (damageDealt * 0.5);

                                        ((ServerWorld) caster.world).spawnParticle(ParticleTypes.DAMAGE_INDICATOR, entity.getPosX(), entity.getPosY() + entity.getHeight() * 0.5, entity.getPosZ(), particles, 0.1, 0, 0.1, 0.2);
                                    }
                                }
                            }
                        } else {
                            entity.attackEntityFrom(DamageSource.causeExplosionDamage(caster), attackDamage);
                        }

                        entity.func_241841_a((ServerWorld) this.world, this);
                    }
                }
            }
        }
    }

    public LivingEntity caster() {
        return this.world.getPlayerByUuid(this.caster);
    }

    public void caster(LivingEntity caster) {
        this.caster = caster.getUniqueID();
    }

    @Override
    public CompoundNBT serializeNBT() {
        var tag = super.serializeNBT();
        tag.putUniqueId("casterUUID", this.caster);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        super.deserializeNBT(tag);

        this.caster = tag.getUniqueId("casterUUID");
    }
}
