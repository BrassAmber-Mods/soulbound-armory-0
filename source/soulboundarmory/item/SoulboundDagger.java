package soulboundarmory.item;

import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.item.weapon.DaggerComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.entity.SoulboundDaggerEntity;
import soulboundarmory.registry.Skills;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

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
        var component = Components.weapon.of(player).item(ItemComponentType.dagger);

        if (!world.isClient && component.hasSkill(Skills.throwing)) {
            player.setCurrentHand(hand);

            return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
        }

        return new TypedActionResult<>(ActionResult.FAIL, player.getStackInHand(hand));
    }

    @Override
    public void onStoppedUsing(ItemStack itemStack, World world, LivingEntity entity, int timeLeft) {
        var player = (PlayerEntity) entity;
        var component = DaggerComponent.get(player);

        if (!world.isClient) {
            var attackSpeed = (float) component.attributeTotal(StatisticType.attackSpeed);
            var velocity = this.getMaxUsageRatio(attackSpeed, timeLeft) * attackSpeed;
            var maxVelocity = velocity / attackSpeed;
            var dagger = new SoulboundDaggerEntity(world, entity, itemStack, component.hasSkill(Skills.shadowClone), velocity, maxVelocity);

            world.spawnEntity(dagger);

            if (!player.isCreative()) {
                player.inventory.removeOne(itemStack);
            }
        }
    }
}
