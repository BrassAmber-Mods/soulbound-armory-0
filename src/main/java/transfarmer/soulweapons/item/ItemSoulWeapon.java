package transfarmer.soulweapons.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.math.Vec3d;
import transfarmer.soulweapons.entity.EntityReachModifier;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static transfarmer.soulweapons.capability.SoulWeaponHelper.ATTACK_DAMAGE_UUID;
import static transfarmer.soulweapons.capability.SoulWeaponHelper.ATTACK_SPEED_UUID;
import static transfarmer.soulweapons.capability.SoulWeaponHelper.REACH_DISTANCE_UUID;

public abstract class ItemSoulWeapon extends ItemSword {
    private final float attackDamage;
    private final float attackSpeed;
    private final float reachDistance;

    public ItemSoulWeapon(final int attackDamage, final float attackSpeed, final float reachDistance) {
        super(ToolMaterial.WOOD);
        this.setMaxDamage(0).setNoRepair();
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.reachDistance = reachDistance;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entity, ItemStack itemStack) {
        if (!entity.world.isRemote && entity instanceof EntityPlayer) {
            final Vec3d look = entity.getLookVec();
            final EntityReachModifier entityReachModifier = new EntityReachModifier(entity.world, entity, this.reachDistance);

            entityReachModifier.shoot(look.x, look.y, look.z);
            entity.world.spawnEntity(entityReachModifier);
        }

        return false;
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }

    public float getAttackSpeed() {
        return this.attackSpeed;
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(final EntityEquipmentSlot slot, final ItemStack itemStack) {
        final Multimap<String, AttributeModifier> attributeModifiers = HashMultimap.create();

        itemStack.addAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_UUID, "generic.attackSpeed", this.attackSpeed, ADD), MAINHAND);
        itemStack.addAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_UUID, "generic.attackDamage", this.attackDamage, ADD), MAINHAND);
        itemStack.addAttributeModifier(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(REACH_DISTANCE_UUID, "generic.reachDistance", this.reachDistance - 3, ADD), MAINHAND);

        return itemStack.getAttributeModifiers(MAINHAND);
    }
}