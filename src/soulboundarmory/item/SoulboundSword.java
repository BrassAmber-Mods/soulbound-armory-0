package soulboundarmory.item;

import soulboundarmory.component.soulbound.item.tool.PickStorage;
import soulboundarmory.component.soulbound.item.weapon.SwordStorage;
import soulboundarmory.entity.SoulboundLightningEntity;
import soulboundarmory.registry.Skills;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;

public class SoulboundSword extends SoulboundMeleeWeapon {
    public SoulboundSword() {
        super(3, -2.4F, 0);
    }

    @Override
    public UseAction getUseAnimation(ItemStack p_77661_1_) {
        return UseAction.BLOCK;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        var component = (SwordStorage) PickStorage.get(player, this).get();

        if (!world.isClientSide && component.hasSkill(Skills.summonLightning) && component.getLightningCooldown() <= 0) {
            var pos = player.position();
            var result = world.clip(new RayTraceContext(pos, pos.add(player.getLookAngle()).multiply(512, 512, 512), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player));

            if (result != null) {
                player.level.addFreshEntity(new SoulboundLightningEntity(player.level, result.getLocation(), player.getUUID()));
                component.resetLightningCooldown();

                return new ActionResult<>(ActionResultType.SUCCESS, player.getItemInHand(hand));
            }
        }

        return new ActionResult<>(ActionResultType.FAIL, player.getItemInHand(hand));
    }
}
