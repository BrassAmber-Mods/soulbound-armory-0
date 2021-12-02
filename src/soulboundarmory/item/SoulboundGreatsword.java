package soulboundarmory.item;

import soulboundarmory.component.soulbound.item.weapon.GreatswordStorage;
import soulboundarmory.registry.Skills;
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
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
        public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (!world.isRemote && GreatswordStorage.get(player).hasSkill(Skills.leaping)) {
            player.setActiveHand(hand);

            return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
        }

        return new ActionResult<>(ActionResultType.FAIL, player.getHeldItem(hand));
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, LivingEntity player, int timeLeft) {
        var timeTaken = 200 - timeLeft;

        if (timeTaken > 5) {
            var look = player.getLookVec();
            var maxSpeed = 1.25F;
            var speed = Math.min(maxSpeed, timeTaken / 20F * maxSpeed);

            player.addVelocity(look.x * speed, look.y * speed / 4 + 0.2, look.z * speed);
            player.setSprinting(true);
            GreatswordStorage.get(player).leapForce(speed / maxSpeed);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void inventoryTick(ItemStack itemStack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isRemote && isSelected) {
            var player = (ClientPlayerEntity) entity;

            if (player.getActiveItemStack().getItem() == this) {
                player.moveForward *= 4.5;
                player.moveStrafing *= 4.5;
            }
        }
    }
}
