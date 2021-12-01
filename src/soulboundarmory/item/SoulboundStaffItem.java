package soulboundarmory.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import soulboundarmory.component.soulbound.item.weapon.StaffStorage;
import soulboundarmory.entity.SoulboundFireballEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class SoulboundStaffItem extends TieredItem implements SoulboundWeaponItem {
    public SoulboundStaffItem() {
        super(SoulboundToolMaterial.SOULBOUND, new Properties().tab(ItemGroup.TAB_COMBAT));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClientSide) {
            var component = StaffStorage.get(user);

            if (component.getFireballCooldown() <= 0) {
                world.addFreshEntity(new SoulboundFireballEntity(world, user, component.spell()));

                if (!user.isCreative()) {
                    component.resetFireballCooldown();
                }

                return new ActionResult<>(ActionResultType.SUCCESS, user.getItemInHand(hand));
            }
        }

        return super.use(world, user, hand);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        return HashMultimap.create();
    }
}
