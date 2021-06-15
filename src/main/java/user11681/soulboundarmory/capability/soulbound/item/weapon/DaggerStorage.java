package user11681.soulboundarmory.capability.soulbound.item.weapon;

import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeMod;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.capability.statistics.EnchantmentStorage;
import user11681.soulboundarmory.capability.statistics.SkillStorage;
import user11681.soulboundarmory.capability.statistics.Statistic;
import user11681.soulboundarmory.capability.statistics.StatisticType;
import user11681.soulboundarmory.capability.statistics.Statistics;
import user11681.soulboundarmory.client.gui.screen.tab.StatisticEntry;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.entity.SAAttributes;
import user11681.soulboundarmory.registry.Skills;
import user11681.soulboundarmory.text.Translation;
import user11681.soulboundarmory.util.AttributeModifierIdentifiers;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;
import static user11681.soulboundarmory.SoulboundArmory.impact;
import static user11681.soulboundarmory.capability.statistics.Category.attribute;
import static user11681.soulboundarmory.capability.statistics.Category.datum;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attackDamage;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attackRange;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attackSpeed;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attributePoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.criticalStrikeProbability;
import static user11681.soulboundarmory.capability.statistics.StatisticType.efficiency;
import static user11681.soulboundarmory.capability.statistics.StatisticType.enchantmentPoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.experience;
import static user11681.soulboundarmory.capability.statistics.StatisticType.level;
import static user11681.soulboundarmory.capability.statistics.StatisticType.reach;
import static user11681.soulboundarmory.capability.statistics.StatisticType.skillPoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.spentAttributePoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.spentEnchantmentPoints;

public class DaggerStorage extends WeaponStorage<DaggerStorage> {
    public DaggerStorage(SoulboundCapability component, Item item) {
        super(component, item);

        this.statistics = Statistics.create()
            .category(datum, experience, level, skillPoints, attributePoints, enchantmentPoints, spentAttributePoints, spentEnchantmentPoints)
            .category(attribute, attackSpeed, attackDamage, criticalStrikeProbability, efficiency, attackRange, reach)
            .min(2, attackSpeed, attackDamage, reach)
            .max(1, criticalStrikeProbability).build();
        this.enchantments = new EnchantmentStorage((Enchantment enchantment) -> {
            String name = enchantment.getFullname(1).getContents().toLowerCase();

            return enchantment.canEnchant(this.itemStack) && !Arrays.asList(UNBREAKING, VANISHING_CURSE).contains(enchantment)
                && (enchantment == impact || !name.contains("soulbound")) && !name.contains("holding")
                && !name.contains("mending");
        });
        this.skills = new SkillStorage(Skills.nourishment, Skills.throwing, Skills.shadowClone, Skills.returning, Skills.sneakReturn);
    }

    public static DaggerStorage get(Entity entity) {
        return Capabilities.weapon.get(entity).storage(StorageType.dagger);
    }

    @Override
    public ITextComponent getName() {
        return Translations.soulboundDagger;
    }

    @Override
    public List<StatisticEntry> getScreenAttributes() {
        List<StatisticEntry> entries = new ReferenceArrayList<>();
        Statistic critical = this.statistic(criticalStrikeProbability);

        entries.add(new StatisticEntry(this.statistic(attackSpeed), new Translation("%s%s: %s", Translations.attackSpeedFormat, Translations.attackSpeedName, this.formatStatistic(attackSpeed))));
        entries.add(new StatisticEntry(this.statistic(attackDamage), new Translation("%s%s: %s", Translations.attackDamageFormat, Translations.attackDamageName, this.formatStatistic(attackDamage))));
        entries.add(new StatisticEntry(critical, new Translation("%s%s: %s%%", Translations.criticalStrikeProbabilityFormat, Translations.criticalStrikeProbabilityName, statisticFormat.format(critical.doubleValue() * 100))));
        entries.add(new StatisticEntry(this.statistic(efficiency), new Translation("%s%s: %s", Translations.weaponEfficiencyFormat, Translations.weaponEfficiencyName, this.formatStatistic(efficiency))));

        return entries;
    }

    @Override
    public List<ITextComponent> getTooltip() {
        NumberFormat format = DecimalFormat.getInstance();
        List<ITextComponent> tooltip = new ArrayList<>();
        Translation text = new Translation(" %s%s %s", format.format(this.attribute(attackSpeed)), Translations.attackSpeedName);
        text.withStyle(style -> style.withColor(TextFormatting.getByCode(Translations.attackSpeedFormat.getKey().charAt(1))));

        tooltip.add(text);
        tooltip.add(new Translation(" %s%s %s", Translations.attackDamageFormat, format.format(this.attributeTotal(attackDamage)), Translations.attackDamageName));
        tooltip.add(new Translation(""));
        tooltip.add(new Translation(""));

        if (this.attribute(criticalStrikeProbability) > 0) {
            tooltip.add(new StringTextComponent(String.format(" %s%s%% %s", Translations.criticalStrikeProbabilityFormat, format.format(this.attribute(criticalStrikeProbability) * 100), Translations.criticalStrikeProbabilityName)));
        }

        if (this.attribute(efficiency) > 0) {
            tooltip.add(new StringTextComponent(String.format(" %s%s %s", Translations.toolEfficiencyFormat, format.format(this.attribute(efficiency)), Translations.toolEfficiencyName)));
        }

        return tooltip;
    }

    @Override
    public Item getConsumableItem() {
        return Items.STONE_SWORD;
    }

    @Override
    public double getIncrease(StatisticType statistic, int points) {
        if (statistic == attackSpeed) return 0.04;
        if (statistic == attackDamage) {return 0.05;} else {
            if (statistic == criticalStrikeProbability) {return 0.02;} else {
                if (statistic == efficiency) return 0.06;
                return 0;
            }
        }
    }

    @Override
    public StorageType<DaggerStorage> type() {
        return StorageType.dagger;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(Multimap<Attribute, AttributeModifier> modifiers, EquipmentSlotType slot) {
        if (slot == EquipmentSlotType.MAINHAND) {
            modifiers.put(Attributes.ATTACK_SPEED, new AttributeModifier(AttributeModifierIdentifiers.attackSpeedModifier, "Weapon modifier", this.attributeRelative(attackSpeed), AttributeModifier.Operation.ADDITION));
            modifiers.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(AttributeModifierIdentifiers.attackDamageModifier, "Weapon modifier", this.attributeRelative(attackDamage), AttributeModifier.Operation.ADDITION));
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(SAAttributes.attackRangeUUID, "Weapon modifier", this.attributeRelative(attackRange), AttributeModifier.Operation.ADDITION));
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(SAAttributes.reachUUID, "Tool modifier", this.attributeRelative(reach), AttributeModifier.Operation.ADDITION));
        }

        return modifiers;
    }
}
