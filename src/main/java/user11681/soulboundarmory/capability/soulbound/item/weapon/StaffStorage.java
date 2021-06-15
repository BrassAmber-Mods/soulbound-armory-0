package user11681.soulboundarmory.capability.soulbound.item.weapon;

import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.capability.statistics.EnchantmentStorage;
import user11681.soulboundarmory.capability.statistics.SkillStorage;
import user11681.soulboundarmory.capability.statistics.StatisticType;
import user11681.soulboundarmory.capability.statistics.Statistics;
import user11681.soulboundarmory.client.gui.screen.tab.StatisticEntry;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.registry.Skills;
import user11681.soulboundarmory.registry.SoulboundItems;
import user11681.soulboundarmory.text.Translation;
import user11681.soulboundarmory.util.Util;
import user11681.soulboundarmory.util.AttributeModifierIdentifiers;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;
import static user11681.soulboundarmory.SoulboundArmory.impact;
import static user11681.soulboundarmory.capability.statistics.Category.attribute;
import static user11681.soulboundarmory.capability.statistics.Category.datum;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attackDamage;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attackSpeed;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attributePoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.criticalStrikeProbability;
import static user11681.soulboundarmory.capability.statistics.StatisticType.enchantmentPoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.experience;
import static user11681.soulboundarmory.capability.statistics.StatisticType.level;
import static user11681.soulboundarmory.capability.statistics.StatisticType.skillPoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.spentAttributePoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.spentEnchantmentPoints;

public class StaffStorage extends WeaponStorage<StaffStorage> {
    protected int fireballCooldown;
    protected int spell;

    public StaffStorage(SoulboundCapability component, Item item) {
        super(component, item);
        this.statistics = Statistics.create()
                .category(datum, experience, level, skillPoints, attributePoints, enchantmentPoints, spentAttributePoints, spentEnchantmentPoints)
                .category(attribute, attackSpeed, attackDamage, criticalStrikeProbability)
                .min(0.48, attackSpeed).min(8, attackDamage)
                .max(1, criticalStrikeProbability).build();
        this.enchantments = new EnchantmentStorage((Enchantment enchantment) -> {
             String name = enchantment.getFullname(1).getContents().toLowerCase();

            return enchantment.canEnchant(this.itemStack) && !Util.contains(enchantment, UNBREAKING, VANISHING_CURSE)
                    && (enchantment == impact || !name.contains("soulbound")) && !name.contains("holding")
                    && !name.contains("mending");
        });
        this.skills = new SkillStorage(Skills.healing, Skills.penetration, Skills.vulnerability, Skills.penetration, Skills.endermanacle);
    }

    public static StaffStorage get(Entity entity) {
        return Capabilities.weapon.get(entity).storage(StorageType.staff);
    }

    @Override
    public ITextComponent getName() {
        return Translations.soulboundStaff;
    }

    @Override
    public StorageType<StaffStorage> type() {
        return StorageType.staff;
    }

    public int getFireballCooldown() {
        return this.fireballCooldown;
    }

    public void setFireballCooldown(int ticks) {
        this.fireballCooldown = ticks;
    }

    public void resetFireballCooldown() {
        this.fireballCooldown = (int) Math.round(20 / this.attribute(attackSpeed));
    }

    public int spell() {
        return this.spell;
    }

    public void setSpell(int spell) {
        this.spell = spell;
    }

    public void cycleSpells(int spells) {
        this.spell = Math.abs((this.spell + spells) % 2);

        this.sync();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(Multimap<Attribute, AttributeModifier> modifiers, EquipmentSlotType slot) {
        if (slot == EquipmentSlotType.MAINHAND) {
            modifiers.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(AttributeModifierIdentifiers.attackSpeedModifier, "Weapon modifier", this.attributeRelative(attackSpeed), AttributeModifier.Operation.ADDITION));
            modifiers.put(Attributes.ATTACK_SPEED, new AttributeModifier(AttributeModifierIdentifiers.attackDamageModifier, "Weapon modifier", this.attributeRelative(attackDamage), AttributeModifier.Operation.ADDITION));
        }

        return modifiers;
    }

    @Override
    public List<StatisticEntry> getScreenAttributes() {
         List<StatisticEntry> entries = new ReferenceArrayList<>();

        entries.add(new StatisticEntry(this.statistic(attackSpeed), new Translation("%s%s: %s", Translations.attackSpeedFormat, Translations.attackSpeedName, this.formatStatistic(attackSpeed))));
        entries.add(new StatisticEntry(this.statistic(attackDamage), new Translation("%s%s: %s", Translations.attackDamageFormat, Translations.attackDamageName, this.formatStatistic(attackDamage))));
        entries.add(new StatisticEntry(this.statistic(criticalStrikeProbability), new Translation("%s%s: %s%%", Translations.criticalStrikeProbabilityFormat, Translations.criticalStrikeProbabilityName, this.formatStatistic(criticalStrikeProbability))));

        return entries;
    }

    @Override
    public List<ITextComponent> getTooltip() {
         NumberFormat format = DecimalFormat.getInstance();
         List<ITextComponent> tooltip = new ArrayList<>();

        tooltip.add(new Translation(" %s%s %s", Translations.attackSpeedFormat, format.format(this.attribute(attackSpeed)), Translations.attackSpeedName));
        tooltip.add(new Translation(" %s%s %s", Translations.attackDamageFormat, format.format(this.attributeTotal(attackDamage)), Translations.attackDamageName));
        tooltip.add(new StringTextComponent(""));
        tooltip.add(new StringTextComponent(""));

        if (this.attribute(criticalStrikeProbability) > 0) {
            tooltip.add(new StringTextComponent(String.format(" %s%s%% %s", Translations.criticalStrikeProbabilityFormat, format.format(this.attribute(criticalStrikeProbability) * 100), Translations.criticalStrikeProbabilityName)));
        }

        return tooltip;
    }

    @Override
    public Item getConsumableItem() {
        return SoulboundItems.staff;
    }

    @Override
    public double getIncrease(StatisticType statistic, int points) {
        if (statistic == attackSpeed) {
            return 0.08;
        }

        if (statistic == attackDamage) {
            return 0.2;
        }

        if (statistic == criticalStrikeProbability) {
            return 0.04;
        }

        return 0;
    }

    @Override
    public void tick() {
        if (this.fireballCooldown > 0) {
            this.fireballCooldown--;
        }
    }

    @Override
    public void serializeNBT(CompoundNBT tag) {
        super.serializeNBT(tag);
        
        tag.putInt("spell", this.spell);
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        super.deserializeNBT(tag);

        this.setSpell(tag.getInt("spell"));
    }

    @Override
    public CompoundNBT toClientTag() {
        CompoundNBT tag = super.toClientTag();

        tag.putInt("spell", this.spell);

        return tag;
    }
}
