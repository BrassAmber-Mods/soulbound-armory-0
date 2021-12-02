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
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        var component = (SwordStorage) PickStorage.get(player, this).get();

        if (!world.isRemote && component.hasSkill(Skills.summonLightning) && component.getLightningCooldown() <= 0) {
            var pos = player.getPositionVec();
            var result = world.rayTraceBlocks(new RayTraceContext(pos, pos.add(player.getLookVec()).mul(512, 512, 512), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player));

            if (result != null) {
                player.world.addEntity(new SoulboundLightningEntity(player.world, result.getHitVec(), player.getUniqueID()));
                component.resetLightningCooldown();

                return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
            }
        }

        return new ActionResult<>(ActionResultType.FAIL, player.getHeldItem(hand));
    }
}
