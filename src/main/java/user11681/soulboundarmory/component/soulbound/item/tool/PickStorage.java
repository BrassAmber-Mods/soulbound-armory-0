package user11681.soulboundarmory.component.soulbound.item.tool;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import user11681.soulboundarmory.client.gui.screen.tab.StatisticEntry;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.component.soulbound.item.StorageType;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.component.statistics.EnchantmentStorage;
import user11681.soulboundarmory.component.statistics.SkillStorage;
import user11681.soulboundarmory.component.statistics.StatisticType;
import user11681.soulboundarmory.component.statistics.Statistics;
import user11681.soulboundarmory.entity.SoulboundArmoryAttributes;
import user11681.soulboundarmory.registry.Skills;
import user11681.soulboundarmory.text.StringableText;

import static net.minecraft.enchantment.Enchantments.MENDING;
import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;
import static user11681.soulboundarmory.component.statistics.Category.attribute;
import static user11681.soulboundarmory.component.statistics.Category.datum;
import static user11681.soulboundarmory.component.statistics.StatisticType.attributePoints;
import static user11681.soulboundarmory.component.statistics.StatisticType.efficiency;
import static user11681.soulboundarmory.component.statistics.StatisticType.enchantmentPoints;
import static user11681.soulboundarmory.component.statistics.StatisticType.experience;
import static user11681.soulboundarmory.component.statistics.StatisticType.level;
import static user11681.soulboundarmory.component.statistics.StatisticType.miningLevel;
import static user11681.soulboundarmory.component.statistics.StatisticType.reach;
import static user11681.soulboundarmory.component.statistics.StatisticType.skillPoints;
import static user11681.soulboundarmory.component.statistics.StatisticType.spentAttributePoints;
import static user11681.soulboundarmory.component.statistics.StatisticType.spentEnchantmentPoints;

public class PickStorage extends ToolStorage<PickStorage> {
    public PickStorage(final SoulboundComponent<?> component, final Item item) {
        super(component, item);

        this.statistics = Statistics.create()
            .category(datum, experience, level, skillPoints, attributePoints, enchantmentPoints, spentAttributePoints, spentEnchantmentPoints)
            .category(attribute, efficiency, reach, miningLevel)
            .min(0.5, efficiency).min(2, reach)
            .max(3, miningLevel)
            .build();
        this.enchantments = new EnchantmentStorage((final Enchantment enchantment) -> {
            final String name = enchantment.getName(1).getString().toLowerCase();

            return enchantment.isAcceptableItem(this.itemStack) && !Arrays.asList(UNBREAKING, VANISHING_CURSE, MENDING).contains(enchantment)
                && !Stream.of("soulbound", "holding", "smelt").map(name::contains).reduce(false, (Boolean contains, Boolean value) -> value || contains);
        });
        this.skills = new SkillStorage(Skills.ENDER_PULL, Skills.AMBIDEXTERITY);
    }

    @Override
    public Item getConsumableItem() {
        return Items.WOODEN_PICKAXE;
    }

    @Override
    public Text getName() {
        return Translations.soulboundPick;
    }

    @Override
    public StorageType<PickStorage> getType() {
        return StorageType.pick;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(final EquipmentSlot slot) {
        final Multimap<EntityAttribute, EntityAttributeModifier> modifiers = HashMultimap.create();

        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(ReachEntityAttributes.REACH, new EntityAttributeModifier(SoulboundArmoryAttributes.REACH_MODIFIER_UUID, "Tool modifier", this.getAttributeRelative(reach), EntityAttributeModifier.Operation.ADDITION));
        }

        return modifiers;
    }

    @Override
    public List<StatisticEntry> getScreenAttributes() {
        List<StatisticEntry> entries = new ReferenceArrayList<>();

        entries.add(new StatisticEntry(this.getStatistic(efficiency), new StringableText("%s%s: %s", Translations.toolEfficiencyFormat, Translations.toolEfficiencyName, this.formatStatistic(efficiency))));
        entries.add(new StatisticEntry(this.getStatistic(miningLevel), new StringableText("%s%s: %s (%s)", Translations.miningLevelFormat, Translations.miningLevelName, this.formatStatistic(miningLevel), this.getMiningLevelName())));
        entries.add(new StatisticEntry(this.getStatistic(reach), new StringableText("%s%s: %s", Translations.reachFormat, Translations.attackRangeName, this.formatStatistic(reach))));

        return entries;
    }

    @Override
    public List<Text> getTooltip() {
        final NumberFormat FORMAT = DecimalFormat.getInstance();
        final List<Text> tooltip = new ArrayList<>(5);

        tooltip.add(new LiteralText(String.format(" %s%s %s", Translations.reachFormat, FORMAT.format(this.getAttribute(reach)), Translations.attackRangeName)));
        tooltip.add(new LiteralText(String.format(" %s%s %s", Translations.toolEfficiencyFormat, FORMAT.format(this.getAttribute(efficiency)), Translations.toolEfficiencyName)));
        tooltip.add(new LiteralText(String.format(" %s%s %s", Translations.miningLevelFormat, FORMAT.format(this.getAttribute(miningLevel)), Translations.miningLevelName)));

        tooltip.add(new LiteralText(""));
        tooltip.add(new LiteralText(""));

        return tooltip;
    }

    @Override
    public double getIncrease(StatisticType statistic, final int points) {
        if (statistic == efficiency) {
            return 0.5;
        }

        if (statistic == reach) {
            return 0.1;
        }

        if (statistic == miningLevel) {
            return 0.2;
        }

        return 0;
    }
}
