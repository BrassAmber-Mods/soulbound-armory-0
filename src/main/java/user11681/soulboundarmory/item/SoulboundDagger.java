package user11681.soulboundarmory.item;


import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.soulbound.item.StorageType;
import user11681.soulboundarmory.component.soulbound.item.weapon.DaggerStorage;
import user11681.soulboundarmory.component.statistics.StatisticType;
import user11681.soulboundarmory.entity.SoulboundDaggerEntity;
import user11681.soulboundarmory.registry.Skills;

public class SoulboundDagger extends SoulboundMeleeWeaponItem {
    public SoulboundDagger() {
        super(1, -2, -1);
    }

    @Override
    public int getMaxUseTime(final ItemStack stack) {
        return this.getMaxItemUseDuration();
    }

    public int getMaxItemUseDuration() {
        return 1200;
    }

    public float getMaxUsageRatio(final float attackSpeed, final int timeLeft) {
        return Math.min(attackSpeed / 2 * (this.getMaxItemUseDuration() - timeLeft) / 20F, 1);
    }

    @Override
    public UseAction getUseAction(final ItemStack itemStack) {
        return UseAction.SPEAR;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, final Hand hand) {
        DaggerStorage component = Components.weaponComponent.get(player).getStorage(StorageType.dagger);

        if (!world.isClient && component.hasSkill(Skills.THROWING)) {
            player.setCurrentHand(hand);

            return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
        }

        return new TypedActionResult<>(ActionResult.FAIL, player.getStackInHand(hand));
    }

    @Override
    public void onStoppedUsing(final ItemStack itemStack, final World world, final LivingEntity entity, final int timeLeft) {
        final PlayerEntity player = (PlayerEntity) entity;
        final DaggerStorage component = DaggerStorage.get(player);

        if (!world.isClient) {
            final float attackSpeed = (float) component.getAttributeTotal(StatisticType.attackSpeed);
            final float velocity = this.getMaxUsageRatio(attackSpeed, timeLeft) * attackSpeed;
            final float maxVelocity = velocity / attackSpeed;
            final SoulboundDaggerEntity dagger = new SoulboundDaggerEntity(world, entity, itemStack, component.hasSkill(Skills.SHADOW_CLONE), velocity, maxVelocity);

            world.spawnEntity(dagger);

            if (!player.isCreative()) {
                player.getInventory().removeOne(itemStack);
            }
        }
    }
}
