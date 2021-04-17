package user11681.soulboundarmory.component.soulbound.item.weapon;

import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import user11681.soulboundarmory.client.gui.screen.tab.StatisticEntry;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.soulbound.item.StorageType;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.component.statistics.EnchantmentStorage;
import user11681.soulboundarmory.component.statistics.SkillStorage;
import user11681.soulboundarmory.component.statistics.Statistic;
import user11681.soulboundarmory.component.statistics.StatisticType;
import user11681.soulboundarmory.component.statistics.Statistics;
import user11681.soulboundarmory.entity.SoulboundArmoryAttributes;
import user11681.soulboundarmory.registry.Skills;
import user11681.soulboundarmory.text.StringableText;
import user11681.usersmanual.entity.AttributeModifierIdentifiers;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;
import static net.minecraft.entity.EquipmentSlot.MAINHAND;
import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION;
import static user11681.soulboundarmory.SoulboundArmory.impact;
import static user11681.soulboundarmory.component.statistics.Category.attribute;
import static user11681.soulboundarmory.component.statistics.Category.datum;
import static user11681.soulboundarmory.component.statistics.StatisticType.attackDamage;
import static user11681.soulboundarmory.component.statistics.StatisticType.attackRange;
import static user11681.soulboundarmory.component.statistics.StatisticType.attackSpeed;
import static user11681.soulboundarmory.component.statistics.StatisticType.attributePoints;
import static user11681.soulboundarmory.component.statistics.StatisticType.criticalStrikeProbability;
import static user11681.soulboundarmory.component.statistics.StatisticType.efficiency;
import static user11681.soulboundarmory.component.statistics.StatisticType.enchantmentPoints;
import static user11681.soulboundarmory.component.statistics.StatisticType.experience;
import static user11681.soulboundarmory.component.statistics.StatisticType.level;
import static user11681.soulboundarmory.component.statistics.StatisticType.reach;
import static user11681.soulboundarmory.component.statistics.StatisticType.skillPoints;
import static user11681.soulboundarmory.component.statistics.StatisticType.spentAttributePoints;
import static user11681.soulboundarmory.component.statistics.StatisticType.spentEnchantmentPoints;

;

public class DaggerStorage extends WeaponStorage<DaggerStorage> {
    public DaggerStorage(SoulboundComponent<?> component, Item item) {
        super(component, item);

        this.statistics = Statistics.create()
                .category(datum, experience, level, skillPoints, attributePoints, enchantmentPoints, spentAttributePoints, spentEnchantmentPoints)
                .category(attribute, attackSpeed, attackDamage, criticalStrikeProbability, efficiency, attackRange, reach)
                .min(2, attackSpeed, attackDamage, reach)
                .max(1, criticalStrikeProbability).build();
        this.enchantments = new EnchantmentStorage((final Enchantment enchantment) -> {
            final String name = enchantment.getName(1).asString().toLowerCase();

            return enchantment.isAcceptableItem(this.itemStack) && !Arrays.asList(UNBREAKING, VANISHING_CURSE).contains(enchantment)
                    && (enchantment == impact || !name.contains("soulbound")) && !name.contains("holding")
                    && !name.contains("mending");
        });
        this.skills = new SkillStorage(Skills.NOURISHMENT, Skills.THROWING, Skills.SHADOW_CLONE, Skills.RETURN, Skills.SNEAK_RETURN);
    }

    public static DaggerStorage get(final Entity entity) {
        return Components.weaponComponent.get(entity).getStorage(StorageType.dagger);
    }

    @Override
    public Text getName() {
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
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(final Multimap<EntityAttribute, EntityAttributeModifier> modifiers, final EquipmentSlot slot) {
        if (slot == MAINHAND) {
            modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(AttributeModifierIdentifiers.ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", this.getAttributeRelative(attackSpeed), ADDITION));
            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(AttributeModifierIdentifiers.ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.getAttributeRelative(attackDamage), ADDITION));
            modifiers.put(ReachEntityAttributes.ATTACK_RANGE, new EntityAttributeModifier(SoulboundArmoryAttributes.ATTACK_RANGE_MODIFIER_UUID, "Weapon modifier", this.getAttributeRelative(attackRange), ADDITION));
            modifiers.put(ReachEntityAttributes.REACH, new EntityAttributeModifier(SoulboundArmoryAttributes.REACH_MODIFIER_UUID, "Tool modifier", this.getAttributeRelative(reach), ADDITION));
        }

        return modifiers;
    }

    @Override
    public List<StatisticEntry> getScreenAttributes() {
        List<StatisticEntry> entries = new ReferenceArrayList<>();
        Statistic critical = this.getStatistic(criticalStrikeProbability);

        entries.add(new StatisticEntry(this.getStatistic(attackSpeed), new StringableText("%s%s: %s", Translations.attackSpeedFormat, Translations.attackSpeedName, this.formatStatistic(attackSpeed))));
        entries.add(new StatisticEntry(this.getStatistic(attackDamage), new StringableText("%s%s: %s", Translations.attackDamageFormat, Translations.attackDamageName, this.formatStatistic(attackDamage))));
        entries.add(new StatisticEntry(critical, new StringableText("%s%s: %s%%", Translations.criticalStrikeProbabilityFormat, Translations.criticalStrikeProbabilityName, format.format(critical.doubleValue() * 100))));
        entries.add(new StatisticEntry(this.getStatistic(efficiency), new StringableText("%s%s: %s", Translations.weaponEfficiencyFormat, Translations.weaponEfficiencyName, this.formatStatistic(efficiency))));

        return entries;
    }

    @Override
    public List<Text> getTooltip() {
        final NumberFormat format = DecimalFormat.getInstance();
        final List<Text> tooltip = new ArrayList<>();

        final StringableText text = new StringableText(" %s%s %s", format.format(this.getAttribute(attackSpeed)), Translations.attackSpeedName);
        text.styled((final Style style) -> style.withColor(Formatting.byCode(Translations.attackSpeedFormat.getKey().charAt(1))));
        tooltip.add(text);
        tooltip.add(new StringableText(" %s%s %s", Translations.attackDamageFormat, format.format(this.getAttributeTotal(attackDamage)), Translations.attackDamageName));
        tooltip.add(new StringableText(""));
        tooltip.add(new StringableText(""));

        if (this.getAttribute(criticalStrikeProbability) > 0) {
            tooltip.add(new LiteralText(String.format(" %s%s%% %s", Translations.criticalStrikeProbabilityFormat, format.format(this.getAttribute(criticalStrikeProbability) * 100), Translations.criticalStrikeProbabilityName)));
        }

        if (this.getAttribute(efficiency) > 0) {
            tooltip.add(new LiteralText(String.format(" %s%s %s", Translations.toolEfficiencyFormat, format.format(this.getAttribute(efficiency)), Translations.toolEfficiencyName)));
        }

        return tooltip;
    }

    @Override
    public double getIncrease(final StatisticType statistic, final int points) {
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
