package net.auoeke.soulboundarmory.item;

import net.auoeke.soulboundarmory.capability.soulbound.item.weapon.GreatswordStorage;
import net.auoeke.soulboundarmory.registry.Skills;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SoulboundGreatsword extends SoulboundMeleeWeapon {
    public SoulboundGreatsword() {
        super(5, -3.2F, 3);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 200;
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
        public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClientSide && GreatswordStorage.get(player).hasSkill(Skills.leaping)) {
            player.startUsingItem(hand);

            return new ActionResult<>(ActionResultType.SUCCESS, player.getItemInHand(hand));
        }

        return new ActionResult<>(ActionResultType.FAIL, player.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(ItemStack itemStack, World world, LivingEntity player, int timeLeft) {
        var timeTaken = 200 - timeLeft;

        if (timeTaken > 5) {
            var look = player.getLookAngle();
            var maxSpeed = 1.25F;
            var speed = Math.min(maxSpeed, timeTaken / 20F * maxSpeed);

            player.push(look.x * speed, look.y * speed / 4 + 0.2, look.z * speed);
            player.setSprinting(true);
            GreatswordStorage.get(player).leapForce(speed / maxSpeed);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void inventoryTick(ItemStack itemStack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isClientSide && isSelected) {
            var player = (ClientPlayerEntity) entity;

            if (player.getUseItem().getItem() == this) {
                player.zza *= 4.5;
                player.xxa *= 4.5;
            }
        }
    }
}
