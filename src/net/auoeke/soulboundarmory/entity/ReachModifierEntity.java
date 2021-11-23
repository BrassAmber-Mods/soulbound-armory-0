package net.auoeke.soulboundarmory.entity;

public class ReachModifierEntity {
/*
    private float reachDistance;

    public ReachModifierEntity(World worldIn) {
        super(worldIn);
    }

    public ReachModifierEntity(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public ReachModifierEntity(World world, LivingEntity shooter, float reachDistance) {
        this(world, shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());

        this.world = world;
        this.ownerUuid = shooter.getUUID();
        this.reachDistance = reachDistance;
        this.setBoundingBox(new Box(0, 0, 0, 0, 0, 0));
    }

    @Override
    protected ItemStack asItemStack() {
        return null;
    }

    public void tick() {
         Vector3d pos = this.getPos();

        Vector3d newPos = new Vector3d(this.getX() + this.motionX, this.getY() + this.motionY, this.getZ() + this.velocityZ());
        RayTraceContext rayTraceResult = this.world.rayTraceBlocks(pos, newPos, false, true, false);

        if (rayTraceResult != null) {
            newPos = new Vector3d(rayTraceResult.hitVec.x, rayTraceResult.hitVec.y, rayTraceResult.hitVec.z);
        }

         Entity entity = this.findEntityOnPath(pos, newPos);

        if (entity != null) {
            rayTraceResult = new HitResult(entity);
        }

        if (rayTraceResult != null && rayTraceResult.entityHit instanceof PlayerEntity) {

            if (owner instanceof PlayerEntity
                    && !((PlayerEntity) owner).canAttackPlayer((PlayerEntity) rayTraceResult.entityHit)) {
                rayTraceResult = null;
            }
        }

        if (rayTraceResult != null && !ForgeEventFactory.onProjectileImpact(this, rayTraceResult)) {
            this.onHit(rayTraceResult);
        }

        this.setPosition(this.getX(), this.getY(), this.getZ());
        this.doBlockCollisions();
    }

    @Override
    protected void onHit(HitResult result) {
        if (!this.world.isClientSide && result.entityHit != owner && owner instanceof PlayerEntity) {
             Entity target = result.entityHit;
             PlayerEntity player = (PlayerEntity) owner;
             IWeaponComponent component = WeaponProvider.get(player);

            if (target != null) {
                if (this.distanceToHit(result) <= this.reachDistance * this.reachDistance
                    && ForgeHooks.onPlayerAttackTarget(player, target) && target.canBeAttackedWithItem() && !target.hitByEntity(player)) {

                    float attackDamageModifier = (float) player.getAttribute(Attributes.GENERIC_ATTACK_DAMAGE).getAttributeValue();
                    float attackDamageRatio = target instanceof LivingEntity
                        ? EnchantmentHelper.getModifierForCreature(player.getMainHandStack(), ((LivingEntity) target).getCreatureAttribute())
                        : EnchantmentHelper.getModifierForCreature(player.getMainHandStack(), EnumCreatureAttribute.UNDEFINED);

                     double cooldownRatio = component.getAttackRatio(component.getItemType());
                    attackDamageModifier *= 0.2 + cooldownRatio * cooldownRatio * 0.8;
                    attackDamageRatio *= cooldownRatio;

                    if (attackDamageModifier > 0 || attackDamageRatio > 0) {
                         boolean strong = cooldownRatio > 0.9F;
                         boolean knockback = player.isSprinting() && strong;
                        int knockbackModifier = EnchantmentHelper.getKnockbackModifier(player);

                        if (knockback) {
                            player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, player.getSoundCategory(), 1, 1);
                            knockbackModifier++;
                        }

                        boolean critical = strong && player.fallDistance > 0 && !player.onGround && !player.isOnLadder()
                            && !player.isInWater() && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding()
                            && target instanceof LivingEntity && !player.isSprinting();

                         CriticalHitEvent hitResult = ForgeHooks.getCriticalHit(player, target, critical, critical ? 1.5F : 1);
                        critical = hitResult != null;

                        if (critical) {
                            attackDamageModifier *= hitResult.getDamageModifier();
                        }

                        attackDamageModifier += attackDamageRatio;
                         double speed = player.distanceWalkedModified - player.prevDistanceWalkedModified;
                         boolean sweep = strong && !critical && !knockback && player.onGround && speed < player.getAIMoveSpeed() && player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof ItemSword;
                         int fireAspectModifier = EnchantmentHelper.getFireAspectModifier(player);
                        float initialHealth = 0;
                        boolean burn = false;

                        if (target instanceof LivingEntity) {
                            initialHealth = ((LivingEntity) target).getHealth();

                            if (fireAspectModifier > 0 && !target.isBurning()) {
                                burn = true;
                                target.setFire(1);
                            }
                        }

                         double motionX = target.motionX;
                         double motionY = target.motionY;
                         double velocityZ() = target.velocityZ();

                        if (target.attackEntityFrom(DamageSource.causePlayerDamage(player), attackDamageModifier)) {
                            if (knockbackModifier > 0) {
                                if (target instanceof LivingEntity) {
                                    ((LivingEntity) target).knockBack(player, knockbackModifier * 0.5F, MathHelper.sin(player.yaw * 0.017453292F), -MathHelper.cos(player.yaw * 0.017453292F));
                                } else {
                                    target.addVelocity(-MathHelper.sin(player.yaw * 0.017453292F) * knockbackModifier * 0.5F, 0.1D, MathHelper.cos(player.yaw * 0.017453292F) * knockbackModifier * 0.5F);
                                }

                                player.motionX *= 0.6D;
                                player.velocityZ() *= 0.6D;
                                player.setSprinting(false);
                            }

                            if (sweep) {
                                float attackDamage = 1.0F + EnchantmentHelper.getSweepingDamageRatio(player) * attackDamageModifier;

                                for (LivingEntity entity : player.world.getEntitiesWithinAABB(LivingEntity.class, target.getEntityBoundingBox())) {
                                    if (entity != player && entity != target && !player.isOnSameTeam(entity) && player.getDistanceSq(entity) < this.reachDistance * this.reachDistance) {
                                        entity.knockBack(player, 0.4F, MathHelper.sin(player.yaw * 0.017453292F), -MathHelper.cos(player.yaw * 0.017453292F));
                                        entity.attackEntityFrom(DamageSource.causePlayerDamage(player), attackDamage);
                                    }
                                }

                                player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1, 1);
                                player.spawnSweepParticles();
                            }

                            if (target instanceof PlayerEntityMP && target.velocityChanged) {
                                ((PlayerEntityMP) target).connection.sendPacket(new SPacketEntityVelocity(target));
                                target.velocityChanged = false;
                                target.motionX = motionX;
                                target.motionY = motionY;
                                target.velocityZ() = velocityZ();
                            }

                            if (critical) {
                                player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), 1, 1);
                                player.onCriticalHit(target);
                            }

                            if (!critical && !sweep) {
                                if (strong) {
                                    player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, player.getSoundCategory(), 1, 1);
                                } else {
                                    player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), 1, 1);
                                }
                            }

                            if (attackDamageRatio > 0) {
                                player.onEnchantmentCritical(target);
                            }

                            player.setLastAttackedEntity(target);

                            if (target instanceof LivingEntity) {
                                EnchantmentHelper.applyThornEnchantments((LivingEntity) target, player);
                            }

                            EnchantmentHelper.applyArthropodEnchantments(player, target);

                            if (target instanceof LivingEntity) {
                                float damageDealt = initialHealth - ((LivingEntity) target).getHealth();
                                player.addStat(StatList.DAMAGE_DEALT, Math.round(damageDealt * 10.0F));

                                if (fireAspectModifier > 0) {
                                    target.setFire(fireAspectModifier * 4);
                                }

                                if (player.world instanceof WorldServer && damageDealt > 2.0F) {
                                    int k = (int) ((double) damageDealt * 0.5D);
                                    ((WorldServer) player.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY() + (double) (target.height * 0.5F), target.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                                }
                            }

                            player.addExhaustion(0.1F);
                        } else {
                            player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, player.getSoundCategory(), 1.0F, 1.0F);

                            if (burn) {
                                target.extinguish();
                            }
                        }
                    }
                }
            }

            component.resetCooldown(component.getItemType());
        }

        this.setDead();
    }

    public void shoot(double x, double y, double z) {
        this.motionX = x * 255;
        this.motionY = y * 255;
        this.velocityZ() = z * 255;
    }

    private double distanceToHit(HitResult rayTraceResult) {
         Vector3d pos = rayTraceResult.hitVec;

        return Math.pow(pos.x - this.getX(), 2) + Math.pow(pos.y - this.getY(), 2) + Math.pow(pos.z - this.getZ(), 2);
    }
*/
}
