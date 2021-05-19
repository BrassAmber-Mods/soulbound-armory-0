package user11681.soulboundarmory.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlotType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.Attribute;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import user11681.soulboundarmory.capability.soulbound.item.weapon.StaffStorage;
import user11681.soulboundarmory.entity.SoulboundFireballEntity;

public class SoulboundStaffItem extends StaffItem implements SoulboundWeaponItem {
    public SoulboundStaffItem() {
        super(ModToolMaterials.SOULBOUND, new Settings());
    }

    @Override
    public boolean postHit(ItemStack stack, final LivingEntity target, final LivingEntity attacker) {
        return true;
    }

    @Override
    public ActionResult<ItemStack> use(World world, final PlayerEntity user, final Hand hand) {
        if (!world.isClientSide) {
            final StaffStorage component = StaffStorage.get(user);

            if (component.getFireballCooldown() <= 0) {
                world.spawnEntity(new SoulboundFireballEntity(world, user, component.spell()));

                if (!user.isCreative()) {
                    component.resetFireballCooldown();
                }

                return new ActionResult<>(ActionResultType.SUCCESS, user.getItemInHand(hand));
            }
        }

        return super.use(world, user, hand);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot) {
        return HashMultimap.create();
    }
}
