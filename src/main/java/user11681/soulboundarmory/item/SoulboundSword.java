package user11681.soulboundarmory.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import user11681.soulboundarmory.capability.soulbound.item.tool.PickStorage;
import user11681.soulboundarmory.capability.soulbound.item.weapon.SwordStorage;
import user11681.soulboundarmory.entity.SoulboundLightningEntity;
import user11681.soulboundarmory.registry.Skills;

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
        SwordStorage component = (SwordStorage) PickStorage.get(player, this);

        if (!world.isClientSide && component.hasSkill(Skills.summonLightning) && component.getLightningCooldown() <= 0) {
            Vector3d pos = player.position();
            BlockRayTraceResult result = world.clip(new RayTraceContext(pos, pos.add(player.getLookAngle()).multiply(512, 512, 512), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player));

            if (result != null) {
                player.level.addFreshEntity(new SoulboundLightningEntity(player.level, result.getLocation(), player.getUUID()));
                component.resetLightningCooldown();

                return new ActionResult<>(ActionResultType.SUCCESS, player.getItemInHand(hand));
            }
        }

        return new ActionResult<>(ActionResultType.FAIL, player.getItemInHand(hand));
    }
}
