package user11681.soulboundarmory.item;


import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
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
    public int getMaxUseTime(ItemStack stack) {
        return this.getMaxItemUseDuration();
    }

    public int getMaxItemUseDuration() {
        return 1200;
    }

    public float getMaxUsageRatio(float attackSpeed, final int timeLeft) {
        return Math.min(attackSpeed / 2 * (this.getMaxItemUseDuration() - timeLeft) / 20F, 1);
    }

    @Override
    public UseAction getUseAction(ItemStack itemStack) {
        return UseAction.SPEAR;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, final Hand hand) {
        DaggerStorage component = Capabilities.weapon.get(player).getStorage(StorageType.dagger);

        if (!world.isClientSide && component.hasSkill(Skills.throwing)) {
            player.setCurrentHand(hand);

            return new ActionResult<>(ActionResultType.SUCCESS, player.getItemInHand(hand));
        }

        return new ActionResult<>(ActionResultType.FAIL, player.getItemInHand(hand));
    }

    @Override
    public void onStoppedUsing(ItemStack itemStack, final World world, final LivingEntity entity, final int timeLeft) {
        final PlayerEntity player = (PlayerEntity) entity;
        final DaggerStorage component = DaggerStorage.get(player);

        if (!world.isClientSide) {
            final float attackSpeed = (float) component.getAttributeTotal(StatisticType.attackSpeed);
            final float velocity = this.getMaxUsageRatio(attackSpeed, timeLeft) * attackSpeed;
            final float maxVelocity = velocity / attackSpeed;
            final SoulboundDaggerEntity dagger = new SoulboundDaggerEntity(world, entity, itemStack, component.hasSkill(Skills.shadowClone), velocity, maxVelocity);

            world.spawnEntity(dagger);

            if (!player.isCreative()) {
                player.getInventory().removeOne(itemStack);
            }
        }
    }
}
