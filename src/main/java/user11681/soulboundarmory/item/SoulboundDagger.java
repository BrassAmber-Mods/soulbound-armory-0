package user11681.soulboundarmory.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.capability.soulbound.item.weapon.DaggerStorage;
import user11681.soulboundarmory.capability.statistics.StatisticType;
import user11681.soulboundarmory.entity.SoulboundDaggerEntity;
import user11681.soulboundarmory.registry.Skills;

public class SoulboundDagger extends user11681.soulboundarmory.item.SoulboundMeleeWeapon {
    public SoulboundDagger() {
        super(1, -2, -1);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return this.getMaxItemUseDuration();
    }

    public int getMaxItemUseDuration() {
        return 1200;
    }

    public float getMaxUsageRatio(float attackSpeed, int timeLeft) {
        return Math.min(attackSpeed / 2 * (this.getMaxItemUseDuration() - timeLeft) / 20F, 1);
    }

    @Override
    public UseAction getUseAnimation(ItemStack p_77661_1_) {
        return UseAction.SPEAR;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        DaggerStorage component = Capabilities.weapon.get(player).storage(StorageType.dagger);

        if (!world.isClientSide && component.hasSkill(Skills.throwing)) {
            player.startUsingItem(hand);

            return new ActionResult<>(ActionResultType.SUCCESS, player.getItemInHand(hand));
        }

        return new ActionResult<>(ActionResultType.FAIL, player.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(ItemStack itemStack, World world, LivingEntity entity, int timeLeft) {
         PlayerEntity player = (PlayerEntity) entity;
         DaggerStorage component = DaggerStorage.get(player);

        if (!world.isClientSide) {
             float attackSpeed = (float) component.attributeTotal(StatisticType.attackSpeed);
             float velocity = this.getMaxUsageRatio(attackSpeed, timeLeft) * attackSpeed;
             float maxVelocity = velocity / attackSpeed;
             SoulboundDaggerEntity dagger = new SoulboundDaggerEntity(world, entity, itemStack, component.hasSkill(Skills.shadowClone), velocity, maxVelocity);

            world.addFreshEntity(dagger);

            if (!player.isCreative()) {
                player.inventory.removeItem(itemStack);
            }
        }
    }
}
