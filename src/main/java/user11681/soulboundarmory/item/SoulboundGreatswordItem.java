package user11681.soulboundarmory.item;

import javax.annotation.Nonnull;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import user11681.soulboundarmory.component.soulbound.item.weapon.GreatswordStorage;
import user11681.soulboundarmory.registry.Skills;

public class SoulboundGreatswordItem extends SoulboundMeleeWeaponItem {
    public SoulboundGreatswordItem() {
        super(5, -3.2F, 3);
    }

    @Override
    public int getMaxUseTime(final ItemStack stack) {
        return 200;
    }

    @Override
        public UseAction getUseAction(final ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
        public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        if (!world.isClient && GreatswordStorage.get(player).hasSkill(Skills.LEAPING)) {
            player.setCurrentHand(hand);

            return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
        }

        return new TypedActionResult<>(ActionResult.FAIL, player.getStackInHand(hand));
    }

    @Override
    public void onStoppedUsing(final ItemStack itemStack, final World world, final LivingEntity player, final int timeLeft) {
        final int timeTaken = 200 - timeLeft;

        if (timeTaken > 5) {
            final Vec3d look = player.getRotationVector();
            final float maxSpeed = 1.25F;
            final float speed = Math.min(maxSpeed, timeTaken / 20F * maxSpeed);

            player.addVelocity(look.x * speed, look.y * speed / 4 + 0.2, look.z * speed);
            player.setSprinting(true);
            GreatswordStorage.get(player).setLeapForce(speed / maxSpeed);
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void inventoryTick(final ItemStack itemStack, final World world, final Entity entity, final int itemSlot, final boolean isSelected) {
        if (world.isClient && isSelected) {
            final ClientPlayerEntity player = (ClientPlayerEntity) entity;
            final ItemStack activeStack = player.getActiveItem();

            if (!activeStack.isEmpty() && activeStack.getItem() == this) {
                player.forwardSpeed *= 4.5;
                player.sidewaysSpeed *= 4.5;
            }
        }
    }
}
