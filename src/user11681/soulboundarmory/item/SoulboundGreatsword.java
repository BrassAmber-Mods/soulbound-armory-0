package user11681.soulboundarmory.item;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import user11681.soulboundarmory.capability.soulbound.item.weapon.GreatswordStorage;
import user11681.soulboundarmory.registry.Skills;

public class SoulboundGreatsword extends SoulboundMeleeWeapon {
    public SoulboundGreatsword() {
        super(5, -3.2F, 3);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 200;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient && GreatswordStorage.get(player).hasSkill(Skills.leaping)) {
            player.setCurrentHand(hand);

            return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
        }

        return new TypedActionResult<>(ActionResult.FAIL, player.getStackInHand(hand));
    }

    @Override
    public void onStoppedUsing(ItemStack itemStack, World world, LivingEntity player, int timeLeft) {
        int timeTaken = 200 - timeLeft;

        if (timeTaken > 5) {
            Vec3d look = player.getRotationVector();
            float maxSpeed = 1.25F;
            float speed = Math.min(maxSpeed, timeTaken / 20F * maxSpeed);

            player.addVelocity(look.x * speed, look.y * speed / 4 + 0.2, look.z * speed);
            player.setSprinting(true);
            GreatswordStorage.get(player).leapForce(speed / maxSpeed);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void inventoryTick(ItemStack itemStack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isClient && isSelected) {
            ClientPlayerEntity player = (ClientPlayerEntity) entity;
            ItemStack activeStack = player.getActiveItem();

            if (!activeStack.isEmpty() && activeStack.getItem() == this) {
                player.forwardSpeed *= 4.5;
                player.sidewaysSpeed *= 4.5;
            }
        }
    }
}
