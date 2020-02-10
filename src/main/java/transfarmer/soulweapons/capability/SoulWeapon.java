package transfarmer.soulweapons.capability;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import transfarmer.soulweapons.Configuration;
import transfarmer.soulweapons.data.SoulWeaponAttribute;
import transfarmer.soulweapons.data.SoulWeaponDatum;
import transfarmer.soulweapons.data.SoulWeaponEnchantment;
import transfarmer.soulweapons.data.SoulWeaponType;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;
import static transfarmer.soulweapons.capability.SoulWeaponHelper.ATTACK_DAMAGE_UUID;
import static transfarmer.soulweapons.capability.SoulWeaponHelper.ATTACK_SPEED_UUID;
import static transfarmer.soulweapons.capability.SoulWeaponHelper.ATTRIBUTES;
import static transfarmer.soulweapons.capability.SoulWeaponHelper.DATA_LENGTH;
import static transfarmer.soulweapons.capability.SoulWeaponHelper.ENCHANTMENTS;
import static transfarmer.soulweapons.capability.SoulWeaponHelper.REACH_DISTANCE_UUID;
import static transfarmer.soulweapons.data.SoulWeaponAttribute.ATTACK_DAMAGE;
import static transfarmer.soulweapons.data.SoulWeaponAttribute.ATTACK_SPEED;
import static transfarmer.soulweapons.data.SoulWeaponAttribute.CRITICAL;
import static transfarmer.soulweapons.data.SoulWeaponAttribute.EFFICIENCY;
import static transfarmer.soulweapons.data.SoulWeaponAttribute.KNOCKBACK_ATTRIBUTE;
import static transfarmer.soulweapons.data.SoulWeaponDatum.ATTRIBUTE_POINTS;
import static transfarmer.soulweapons.data.SoulWeaponDatum.ENCHANTMENT_POINTS;
import static transfarmer.soulweapons.data.SoulWeaponDatum.LEVEL;
import static transfarmer.soulweapons.data.SoulWeaponDatum.SKILLS;
import static transfarmer.soulweapons.data.SoulWeaponDatum.SPENT_ATTRIBUTE_POINTS;
import static transfarmer.soulweapons.data.SoulWeaponDatum.SPENT_ENCHANTMENT_POINTS;
import static transfarmer.soulweapons.data.SoulWeaponDatum.XP;
import static transfarmer.soulweapons.data.SoulWeaponEnchantment.SHARPNESS;
import static transfarmer.soulweapons.data.SoulWeaponType.GREATSWORD;
import static transfarmer.soulweapons.data.SoulWeaponType.SWORD;

public class SoulWeapon implements ISoulWeapon {
    private static final String[][] skills = {
        {"charge"},
        {}, // lightning strike?
        {"throwing", "perforation", "return", "sneak return"}
    };
    private SoulWeaponType currentType;
    private int currentTab = 0;
    private int cooldown = 0;
    private int boundSlot = -1;
    private int[][] data = new int[3][DATA_LENGTH];
    private float[][] attributes = new float[3][ATTRIBUTES];
    private int[][] enchantments = new int[3][ENCHANTMENTS];

    @Override
    public void set(final int[][] data, final float[][] attributes, final int[][] enchantments) {
        this.data = data;
        this.attributes = attributes;
        this.enchantments = enchantments;
    }

    @Override
    public void setData(final int[][] data) {
        this.data = data;
    }

    @Override
    public void setAttributes(final float[][] attributes) {
        this.attributes = attributes;
    }

    @Override
    public void setAttributes(final float[] attributes, final SoulWeaponType type) {
        this.attributes[type.index] = attributes;
    }

    @Override
    public void setEnchantments(final int[][] enchantments) {
        this.enchantments = enchantments;
    }

    @Override
    public void setEnchantments(final int[] enchantments, final SoulWeaponType type) {
        this.enchantments[type.index] = enchantments;
    }

    @Override
    public int[][] getData() {
        return this.data;
    }

    @Override
    public float[][] getAttributes() {
        return this.attributes;
    }

    @Override
    public int[][] getEnchantments() {
        return this.enchantments;
    }

    @Override
    public float getAttribute(final SoulWeaponAttribute attribute, final SoulWeaponType type) {
        switch (attribute) {
            case ATTACK_SPEED:
                return this.attributes[type.index][ATTACK_SPEED.index] + type.item.getAttackSpeed();
            case ATTACK_DAMAGE:
                return this.attributes[type.index][ATTACK_DAMAGE.index] + type.item.getAttackDamage();
            default:
                return this.attributes[type.index][attribute.index];
        }
    }

    @Override
    public void setAttribute(final float value, final SoulWeaponAttribute attribute, final SoulWeaponType type) {
        this.attributes[type.index][attribute.index] = value;
    }

    @Override
    public void addAttribute(final int amount, final SoulWeaponAttribute attribute, final SoulWeaponType type) {
        final int sign = (int) Math.signum(amount);
        for (int i = 0; i < Math.abs(amount); i++) {
            this.addDatum(-sign, ATTRIBUTE_POINTS, type);
            this.addDatum(sign, SPENT_ATTRIBUTE_POINTS, type);

            if ((attribute == CRITICAL && this.getAttribute(CRITICAL, type) + sign * CRITICAL.getIncrease(type) >= 100)) {
                this.setAttribute(100, attribute, type);
                return;
            } else if (this.attributes[type.index][attribute.index] + sign * attribute.getIncrease(type) > 0) {
                this.attributes[type.index][attribute.index] += sign * attribute.getIncrease(type);
            } else {
                this.attributes[type.index][attribute.index] = 0;
                return;
            }
        }
    }

    @Override
    public ItemStack getItemStack(final ItemStack itemStack) {
        return getItemStack(SoulWeaponType.getType(itemStack));
    }

    @Override
    public ItemStack getItemStack(final SoulWeaponType type) {
        final ItemStack itemStack = type.getItemStack();
        final AttributeModifier[] attributeModifiers = getAttributeModifiers(type);
        final SortedMap<SoulWeaponEnchantment, Integer> enchantments = this.getEnchantments(type);

        itemStack.addAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED.getName(), attributeModifiers[0], MAINHAND);
        itemStack.addAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), attributeModifiers[1], MAINHAND);

        if (type == GREATSWORD) {
            itemStack.addAttributeModifier(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(REACH_DISTANCE_UUID, "generic.reachDistance", 3, ADD), MAINHAND);
        } else if (type == SWORD) {
            itemStack.addAttributeModifier(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(REACH_DISTANCE_UUID, "generic.reachDistance", 1.5, ADD), MAINHAND);
        }

        enchantments.forEach((final SoulWeaponEnchantment enchantment, final Integer level) -> itemStack.addEnchantment(enchantment.enchantment, level));

        return itemStack;
    }

    @Override
    public AttributeModifier[] getAttributeModifiers(final SoulWeaponType type) {
        return new AttributeModifier[]{
            new AttributeModifier(ATTACK_SPEED_UUID, "generic.attackSpeed", getAttribute(ATTACK_SPEED, type), ADD),
            new AttributeModifier(ATTACK_DAMAGE_UUID, "generic.attackDamage", getAttribute(ATTACK_DAMAGE, type), ADD)
        };
    }

    @Override
    public SortedMap<SoulWeaponEnchantment, Integer> getEnchantments(final SoulWeaponType type) {
        final SortedMap<SoulWeaponEnchantment, Integer> enchantments = new TreeMap<>();

        for (final SoulWeaponEnchantment enchantment : SoulWeaponEnchantment.getEnchantments()) {
            final int level = this.getEnchantment(enchantment, type);

            if (level > 0) {
                enchantments.put(enchantment, level);
            }
        }

        return enchantments;
    }

    @Override
    public String[] getTooltip(final SoulWeaponType type) {
        final NumberFormat FORMAT = DecimalFormat.getInstance();
        final List<String> tooltip = new ArrayList<>(7);
        final Map<SoulWeaponEnchantment, Integer> enchantments = this.getEnchantments(type);

        float attackDamage = this.getAttribute(ATTACK_DAMAGE, type) + 1;

        if (enchantments.containsKey(SHARPNESS)) {
            attackDamage += 1 + (enchantments.get(SHARPNESS) - 1) / 2F;
        }

        tooltip.add(String.format(" %s%s %s", I18n.format("format.soulweapons.attackSpeed"), FORMAT.format(this.getAttribute(ATTACK_SPEED, type) + 4), I18n.format("attribute.soulweapons.attackSpeed")));
        tooltip.add(String.format(" %s%s %s", I18n.format("format.soulweapons.attackDamage"), FORMAT.format(attackDamage), I18n.format("attribute.soulweapons.attackDamage")));

        tooltip.add("");
        tooltip.add("");

        if (this.getAttribute(CRITICAL, type) > 0) {
            tooltip.add(String.format(" %s%s%% %s", I18n.format("format.soulweapons.critical"), FORMAT.format(this.getAttribute(CRITICAL, type)), I18n.format("attribute.soulweapons.critical")));
        } if (this.getAttribute(KNOCKBACK_ATTRIBUTE, type) > 0) {
            tooltip.add(String.format(" %s%s %s", I18n.format("format.soulweapons.knockback"), FORMAT.format(this.getAttribute(KNOCKBACK_ATTRIBUTE, type)), I18n.format("attribute.soulweapons.knockback")));
        } if (this.getAttribute(EFFICIENCY, type) > 0) {
            tooltip.add(String.format(" %s%s %s", I18n.format("format.soulweapons.efficiency"), FORMAT.format(this.getAttribute(EFFICIENCY, type)), I18n.format("attribute.soulweapons.efficiency")));
        }

        return tooltip.toArray(new String[0]);
    }

    @Override
    public int getNextLevelXP(final SoulWeaponType type) {
        return this.getDatum(LEVEL, type) >= Configuration.maxLevel ?
            1 : Configuration.initialXP + 4 * (int) Math.round(Math.pow(this.getDatum(LEVEL, type), 1.5));
    }

    @Override
    public int getDatum(SoulWeaponDatum datum, SoulWeaponType type) {
        if (type == null) {
            return -1;
        }

        return this.data[type.index][datum.index];
    }

    @Override
    public void setDatum(final int value, final SoulWeaponDatum datum, final SoulWeaponType type) {
        this.data[type.index][datum.index] = value;
    }

    @Override
    public boolean addDatum(int amount, SoulWeaponDatum datum, SoulWeaponType type) {
        switch (datum) {
            case XP:
                this.data[type.index][XP.index] += amount;

                if (this.getDatum(XP, type) >= this.getNextLevelXP(type) && this.getDatum(LEVEL, type) < Configuration.maxLevel) {
                    this.addDatum(-this.getNextLevelXP(type), XP, type);
                    this.addDatum(1, LEVEL, type);
                    return true;
                }

                break;
            case LEVEL:
                final int level = ++this.data[type.index][LEVEL.index];
                if (level % (Configuration.levelsPerEnchantment) == 0) {
                    this.addDatum(1, ENCHANTMENT_POINTS, type);
                }

                if (level % (Configuration.levelsPerSkill) == 0 && this.getDatum(SKILLS, type) < this.getMaxSkills(type)) {
                    this.addDatum(1, SKILLS, type);
                }

                this.addDatum(1, ATTRIBUTE_POINTS, type);
                break;
            default:
                this.data[type.index][datum.index] += amount;
        }

        return false;
    }

    @Override
    public int getEnchantment(final SoulWeaponEnchantment enchantment, final SoulWeaponType type) {
        return this.enchantments[type.index][enchantment.index];
    }

    @Override
    public void addEnchantment(final int amount, final SoulWeaponEnchantment enchantment, final SoulWeaponType type) {
        final int sign = (int) Math.signum(amount);

        for (int i = 0; i < Math.abs(amount); i++) {
            if (this.getEnchantment(enchantment, type) + sign >= 0) {
                this.addDatum(-sign, ENCHANTMENT_POINTS, type);
                this.addDatum(sign, SPENT_ENCHANTMENT_POINTS, type);

                this.enchantments[type.index][enchantment.index] += sign;
            } else {
                return;
            }
        }
    }

    @Override
    public void setCurrentTab(final int tab) {
        this.currentTab = tab;
    }

    @Override
    public int getCurrentTab() {
        return this.currentTab;
    }

    @Override
    public SoulWeaponType getCurrentType() {
        return this.currentType;
    }

    @Override
    public void setCurrentType(final SoulWeaponType type) {
        this.currentType = type;
    }

    @Override
    public void setCurrentType(final int index) {
        this.currentType = SoulWeaponType.getType(index);
    }

    @Override
    public void setCooldown(final int ticks) {
        this.cooldown = ticks;
    }

    @Override
    public void resetCooldown(final SoulWeaponType type) {
        this.cooldown = this.getCooldown(type);
    }

    @Override
    public void addCooldown(final int ticks) {
        this.cooldown += ticks;
    }

    @Override
    public int getCooldown() {
        return this.cooldown;
    }

    @Override
    public int getCooldown(final SoulWeaponType type) {
        return Math.round(20 / (4 + this.getAttribute(ATTACK_SPEED, type)));
    }

    @Override
    public float getAttackRatio(final SoulWeaponType type) {
        return 1 - (float) this.getCooldown() / this.getCooldown(type);
    }

    @Override
    public int getBoundSlot() {
        return this.boundSlot;
    }

    @Override
    public void setBoundSlot(final int boundSlot) {
        this.boundSlot = boundSlot;
    }

    @Override
    public void unbindSlot() {
        this.boundSlot = -1;
    }

    @Override
    public int getMaxSkills(SoulWeaponType type) {
        return skills[type.index].length;
    }

    public static String[][] getSkills() {
        return skills;
    }
}
