package user11681.soulboundarmory.capability.soulbound.item.weapon;

import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachAttributes;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlotType;
import net.minecraft.entity.attribute.Attribute;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.text.ITextComponent;
import user11681.soulboundarmory.client.gui.screen.tab.StatisticEntry;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.capability.statistics.EnchantmentStorage;
import user11681.soulboundarmory.capability.statistics.SkillStorage;
import user11681.soulboundarmory.capability.statistics.Statistic;
import user11681.soulboundarmory.capability.statistics.StatisticType;
import user11681.soulboundarmory.capability.statistics.Statistics;
import user11681.soulboundarmory.entity.SAAttributes;
import user11681.soulboundarmory.registry.Skills;
import user11681.soulboundarmory.text.Translation;
import user11681.soulboundarmory.util.AttributeModifierIdentifiers;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;
import static net.minecraft.entity.EquipmentSlotType.MAINHAND;
import static net.minecraft.entity.attribute.AttributeModifier.Operation.ADDITION;
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

;

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
        return Capabilities.weapon.get(entity).getStorage(StorageType.dagger);
    }

    @Override
    public ITextComponent getName() {
        return Translations.soulboundDagger;
    }

    @Override
    public StorageType<DaggerStorage> getType() {
        return StorageType.dagger;
    }

    @Override
    public Item getConsumableItem() {
        return Items.STONE_SWORD;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(Multimap<Attribute, AttributeModifier> modifiers, final EquipmentSlotType slot) {
        if (slot == MAINHAND) {
            modifiers.put(Attributes.ATTACK_SPEED, new AttributeModifier(AttributeModifierIdentifiers.ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", this.getAttributeRelative(attackSpeed), ADDITION));
            modifiers.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(AttributeModifierIdentifiers.ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.getAttributeRelative(attackDamage), ADDITION));
            modifiers.put(ReachAttributes.ATTACK_RANGE, new AttributeModifier(SAAttributes.attackRangeUUID, "Weapon modifier", this.getAttributeRelative(attackRange), ADDITION));
            modifiers.put(ReachAttributes.REACH, new AttributeModifier(SAAttributes.reachUUID, "Tool modifier", this.getAttributeRelative(reach), ADDITION));
        }

        return modifiers;
    }

    @Override
    public List<StatisticEntry> getScreenAttributes() {
        List<StatisticEntry> entries = new ReferenceArrayList<>();
        Statistic critical = this.getStatistic(criticalStrikeProbability);

        entries.add(new StatisticEntry(this.getStatistic(attackSpeed), new Translation("%s%s: %s", Translations.attackSpeedFormat, Translations.attackSpeedName, this.formatStatistic(attackSpeed))));
        entries.add(new StatisticEntry(this.getStatistic(attackDamage), new Translation("%s%s: %s", Translations.attackDamageFormat, Translations.attackDamageName, this.formatStatistic(attackDamage))));
        entries.add(new StatisticEntry(critical, new Translation("%s%s: %s%%", Translations.criticalStrikeProbabilityFormat, Translations.criticalStrikeProbabilityName, format.format(critical.doubleValue() * 100))));
        entries.add(new StatisticEntry(this.getStatistic(efficiency), new Translation("%s%s: %s", Translations.weaponEfficiencyFormat, Translations.weaponEfficiencyName, this.formatStatistic(efficiency))));

        return entries;
    }

    @Override
    public List<ITextComponent> getTooltip() {
        final NumberFormat format = DecimalFormat.getInstance();
        final List<ITextComponent> tooltip = new ArrayList<>();

        final Translation text = new Translation(" %s%s %s", format.format(this.getAttribute(attackSpeed)), Translations.attackSpeedName);
        text.styled((Style style) -> style.withColor(Formatting.byCode(Translations.attackSpeedFormat.getKey().charAt(1))));
        tooltip.add(text);
        tooltip.add(new Translation(" %s%s %s", Translations.attackDamageFormat, format.format(this.getAttributeTotal(attackDamage)), Translations.attackDamageName));
        tooltip.add(new Translation(""));
        tooltip.add(new Translation(""));

        if (this.getAttribute(criticalStrikeProbability) > 0) {
            tooltip.add(new StringTextComponent(String.format(" %s%s%% %s", Translations.criticalStrikeProbabilityFormat, format.format(this.getAttribute(criticalStrikeProbability) * 100), Translations.criticalStrikeProbabilityName)));
        }

        if (this.getAttribute(efficiency) > 0) {
            tooltip.add(new StringTextComponent(String.format(" %s%s %s", Translations.toolEfficiencyFormat, format.format(this.getAttribute(efficiency)), Translations.toolEfficiencyName)));
        }

        return tooltip;
    }

    @Override
    public double getIncrease(StatisticType statistic, final int points) {
        return statistic == attackSpeed
                ? 0.04
                : statistic == attackDamage
                ? 0.05
                : statistic == criticalStrikeProbability
                ? 0.02
                : statistic == efficiency
                ? 0.06
                : 0;
    }
}
