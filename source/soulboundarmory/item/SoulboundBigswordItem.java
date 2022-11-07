package soulboundarmory.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.item.weapon.BigswordComponent;
import soulboundarmory.config.Configuration;

public class SoulboundBigswordItem extends SoulboundMeleeWeapon {
	public SoulboundBigswordItem() {
		super(4, 1, 3);
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 200;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		player.setCurrentHand(hand);
		return TypedActionResult.pass(player.getStackInHand(hand));
	}

	@Override
	public void onStoppedUsing(ItemStack itemStack, World world, LivingEntity user, int timeLeft) {
		ItemComponentType.bigsword.nullable(user).filter(BigswordComponent::canCharge).ifPresent(component -> {
			var look = user.getRotationVector();
			var speed = 1.5;

			if (user.isInFluidType()) {
				speed *= Configuration.Items.Bigsword.fluidChargeAccelerationFactor;
			}

			user.addVelocity(look.x * speed, 0, look.z * speed);
			component.charge();
		});
	}
}
