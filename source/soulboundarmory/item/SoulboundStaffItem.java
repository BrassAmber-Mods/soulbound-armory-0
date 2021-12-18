package soulboundarmory.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.entity.SoulboundFireballEntity;
import soulboundarmory.registry.SoulboundItems;

public class SoulboundStaffItem extends ToolItem implements SoulboundWeaponItem {
    public SoulboundStaffItem() {
        super(SoulboundItems.material, new Settings().group(ItemGroup.COMBAT));
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            var component = ItemComponentType.staff.of(user);

            if (component.fireballCooldown() <= 0) {
                world.spawnEntity(new SoulboundFireballEntity(world, user, component.spell()));

                if (!user.isCreative()) {
                    component.resetFireballCooldown();
                }

                return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
            }
        }

        return super.use(world, user, hand);
    }
}
