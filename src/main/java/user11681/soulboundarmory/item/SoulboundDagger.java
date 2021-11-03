package user11681.soulboundarmory.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.capability.soulbound.item.weapon.DaggerStorage;
import user11681.soulboundarmory.capability.statistics.StatisticType;
import user11681.soulboundarmory.entity.SoulboundDaggerEntity;
import user11681.soulboundarmory.registry.Skills;

public class SoulboundDagger extends SoulboundMeleeWeapon {
    private static final int USE_TIME = 1200;

    public SoulboundDagger() {
        super(1, -2, -1);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return USE_TIME;
    }

    public float getMaxUsageRatio(float attackSpeed, int timeLeft) {
        return Math.min(attackSpeed / 2 * (USE_TIME - timeLeft) / 20F, 1);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        DaggerStorage component = Capabilities.weapon.get(player).storage(StorageType.dagger);

        if (!world.isClient && component.hasSkill(Skills.throwing)) {
            player.setCurrentHand(hand);

            return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
        }

        return new TypedActionResult<>(ActionResult.FAIL, player.getStackInHand(hand));
    }

    @Override
    public void onStoppedUsing(ItemStack itemStack, World world, LivingEntity entity, int timeLeft) {
         PlayerEntity player = (PlayerEntity) entity;
         DaggerStorage component = DaggerStorage.get(player);

        if (!world.isClient) {
             float attackSpeed = (float) component.attributeTotal(StatisticType.attackSpeed);
             float velocity = this.getMaxUsageRatio(attackSpeed, timeLeft) * attackSpeed;
             float maxVelocity = velocity / attackSpeed;
             SoulboundDaggerEntity dagger = new SoulboundDaggerEntity(world, entity, itemStack, component.hasSkill(Skills.shadowClone), velocity, maxVelocity);

            world.spawnEntity(dagger);

            if (!player.isCreative()) {
                player.inventory.removeOne(itemStack);
            }
        }
    }
}
