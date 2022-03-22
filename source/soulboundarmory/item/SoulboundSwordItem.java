package soulboundarmory.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.item.weapon.SwordComponent;
import soulboundarmory.entity.SoulboundLightningEntity;
import soulboundarmory.skill.Skills;

public class SoulboundSwordItem extends SoulboundMeleeWeapon {
    public SoulboundSwordItem() {
        super(3, -2.4F, 0);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        var stack = player.getStackInHand(hand);
        var component = (SwordComponent) ItemComponent.of(player, stack).get();

        if (!world.isClient && component.hasSkill(Skills.summonLightning) && component.lightningCooldown() <= 0) {
            var pos = player.getPos();
            var result = world.raycast(new RaycastContext(pos, pos.add(player.getRotationVector()).multiply(512, 512, 512), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));

            if (result != null) {
                player.world.spawnEntity(new SoulboundLightningEntity(player.world, result.getPos(), player.getUuid()));
                component.resetLightningCooldown();

                return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
            }
        }

        return new TypedActionResult<>(ActionResult.FAIL, player.getStackInHand(hand));
    }
}
