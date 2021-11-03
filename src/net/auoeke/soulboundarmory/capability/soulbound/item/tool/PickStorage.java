package net.auoeke.soulboundarmory.capability.soulbound.item.tool;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.auoeke.soulboundarmory.capability.soulbound.item.StorageType;
import net.auoeke.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import net.auoeke.soulboundarmory.capability.statistics.Category;
import net.auoeke.soulboundarmory.capability.statistics.SkillStorage;
import net.auoeke.soulboundarmory.capability.statistics.StatisticType;
import net.auoeke.soulboundarmory.capability.statistics.Statistics;
import net.auoeke.soulboundarmory.client.gui.screen.StatisticEntry;
import net.auoeke.soulboundarmory.client.i18n.Translations;
import net.auoeke.soulboundarmory.entity.SAAttributes;
import net.auoeke.soulboundarmory.registry.Skills;
import net.auoeke.soulboundarmory.text.Translation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraftforge.common.ForgeMod;
import net.auoeke.soulboundarmory.capability.statistics.EnchantmentStorage;

import static net.minecraft.enchantment.Enchantments.MENDING;
import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;

public class PickStorage extends ToolStorage<PickStorage> {
    public PickStorage(SoulboundCapability component, Item item) {
        super(component, item);

        this.statistics = Statistics.create()
            .category(Category.datum, StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints, StatisticType.spentAttributePoints, StatisticType.spentEnchantmentPoints)
            .category(Category.attribute, StatisticType.efficiency, StatisticType.reach, StatisticType.miningLevel)
            .min(0.5, StatisticType.efficiency).min(2, StatisticType.reach)
            .max(3, StatisticType.miningLevel)
            .build();
        this.enchantments = new EnchantmentStorage((Enchantment enchantment) -> {
             String name = enchantment.getName(1).getString().toLowerCase();

            return enchantment.isAcceptableItem(this.itemStack) && !Arrays.asList(UNBREAKING, VANISHING_CURSE, MENDING).contains(enchantment)
                && !Stream.of("soulbound", "holding", "smelt").map(name::contains).reduce(false, (Boolean contains, Boolean value) -> value || contains);
        });
        this.skills = new SkillStorage(Skills.enderPull, Skills.ambidexterity);
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
    public StorageType<PickStorage> type() {
        return StorageType.pick;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers(EquipmentSlot slot) {
        Multimap<EntityAttribute, EntityAttributeModifier> modifiers = HashMultimap.create();

        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), new EntityAttributeModifier(SAAttributes.reachUUID, "Tool modifier", this.attributeRelative(StatisticType.reach), EntityAttributeModifier.Operation.ADDITION));
        }

        return modifiers;
    }

    @Override
    public List<StatisticEntry> screenAttributes() {
        List<StatisticEntry> entries = new ReferenceArrayList<>();

        entries.add(new StatisticEntry(this.statistic(StatisticType.efficiency), new Translation("%s%s: %s", Translations.toolEfficiencyFormat, Translations.toolEfficiencyName, this.formatStatistic(StatisticType.efficiency))));
        entries.add(new StatisticEntry(this.statistic(StatisticType.miningLevel), new Translation("%s%s: %s (%s)", Translations.miningLevelFormat, Translations.miningLevelName, this.formatStatistic(StatisticType.miningLevel), this.miningLevelName())));
        entries.add(new StatisticEntry(this.statistic(StatisticType.reach), new Translation("%s%s: %s", Translations.reachFormat, Translations.attackRangeName, this.formatStatistic(StatisticType.reach))));

        return entries;
    }

    @Override
    public List<Text> tooltip() {
        NumberFormat FORMAT = DecimalFormat.getInstance();
        List<Text> tooltip = new ArrayList<>(5);

        tooltip.add(new Translation(" %s%s %s", Translations.reachFormat, FORMAT.format(this.attribute(StatisticType.reach)), Translations.attackRangeName));
        tooltip.add(new Translation(" %s%s %s", Translations.toolEfficiencyFormat, FORMAT.format(this.attribute(StatisticType.efficiency)), Translations.toolEfficiencyName));
        tooltip.add(new Translation(" %s%s %s", Translations.miningLevelFormat, FORMAT.format(this.attribute(StatisticType.miningLevel)), Translations.miningLevelName));

        tooltip.add(LiteralText.EMPTY);
        tooltip.add(LiteralText.EMPTY);

        return tooltip;
    }

    @Override
    public double increase(StatisticType statistic, int points) {
        if (statistic == StatisticType.efficiency) {
            return 0.5;
        }

        if (statistic == StatisticType.reach) {
            return 0.1;
        }

        if (statistic == StatisticType.miningLevel) {
            return 0.2;
        }

        return 0;
    }
}
