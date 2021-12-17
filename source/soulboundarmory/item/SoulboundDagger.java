package soulboundarmory.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.entity.SoulboundDaggerEntity;
import soulboundarmory.registry.Skills;

public class SoulboundDagger extends SoulboundMeleeWeapon {
    private static final int USE_TIME = 1200;

    public SoulboundDagger() {
        super(1, -2, -1);
    }

    private static float maxUsageRatio(float attackSpeed, int timeLeft) {
        return Math.min(attackSpeed / 2 * (USE_TIME - timeLeft) / 20F, 1);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return USE_TIME;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (ItemComponentType.dagger.of(player).hasSkill(Skills.throwing)) {
            player.setCurrentHand(hand);

            return TypedActionResult.consume(player.getStackInHand(hand));
        }

        return TypedActionResult.fail(player.getStackInHand(hand));
    }

    @Override
    public void onStoppedUsing(ItemStack itemStack, World world, LivingEntity entity, int timeLeft) {
        if (entity instanceof ServerPlayerEntity player) {
            var component = ItemComponentType.dagger.of(player);
            var attackSpeed = (float) component.attributeTotal(StatisticType.attackSpeed);
            var speed = maxUsageRatio(attackSpeed, timeLeft) * attackSpeed;
            var maxSpeed = speed / attackSpeed;

            world.spawnEntity(new SoulboundDaggerEntity(world, entity, itemStack, component.hasSkill(Skills.shadowClone), speed, maxSpeed));

            if (!player.isCreative()) {
                player.getInventory().removeOne(itemStack);
            }
        }
    }
}
