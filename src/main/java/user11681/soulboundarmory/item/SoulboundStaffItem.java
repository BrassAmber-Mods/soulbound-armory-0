package user11681.soulboundarmory.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import user11681.soulboundarmory.component.soulbound.item.weapon.StaffStorage;
import user11681.soulboundarmory.entity.SoulboundFireballEntity;

public class SoulboundStaffItem extends StaffItem implements SoulboundWeaponItem {
    public SoulboundStaffItem() {
        super(ModToolMaterials.SOULBOUND, new Settings());
    }

    @Override
    public boolean postHit(final ItemStack stack, final LivingEntity target, final LivingEntity attacker) {
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity user, final Hand hand) {
        if (!world.isClient) {
            final StaffStorage component = StaffStorage.get(user);

            if (component.getFireballCooldown() <= 0) {
                world.spawnEntity(new SoulboundFireballEntity(world, user, component.getSpell()));

                if (!user.isCreative()) {
                    component.resetFireballCooldown();
                }

                return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
            }
        }

        return super.use(world, user, hand);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(final EquipmentSlot slot) {
        return HashMultimap.create();
    }
}
