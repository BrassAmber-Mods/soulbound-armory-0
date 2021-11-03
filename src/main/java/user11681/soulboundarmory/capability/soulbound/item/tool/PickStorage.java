package user11681.soulboundarmory.capability.soulbound.item.tool;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
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
import net.minecraftforge.common.ForgeMod;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.capability.statistics.EnchantmentStorage;
import user11681.soulboundarmory.capability.statistics.SkillStorage;
import user11681.soulboundarmory.capability.statistics.StatisticType;
import user11681.soulboundarmory.capability.statistics.Statistics;
import user11681.soulboundarmory.client.gui.screen.StatisticEntry;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.entity.SAAttributes;
import user11681.soulboundarmory.registry.Skills;
import user11681.soulboundarmory.text.Translation;

import static net.minecraft.enchantment.Enchantments.MENDING;
import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;
import static user11681.soulboundarmory.capability.statistics.Category.attribute;
import static user11681.soulboundarmory.capability.statistics.Category.datum;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attributePoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.efficiency;
import static user11681.soulboundarmory.capability.statistics.StatisticType.enchantmentPoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.experience;
import static user11681.soulboundarmory.capability.statistics.StatisticType.level;
import static user11681.soulboundarmory.capability.statistics.StatisticType.miningLevel;
import static user11681.soulboundarmory.capability.statistics.StatisticType.reach;
import static user11681.soulboundarmory.capability.statistics.StatisticType.skillPoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.spentAttributePoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.spentEnchantmentPoints;

public class PickStorage extends ToolStorage<PickStorage> {
    public PickStorage(SoulboundCapability component, Item item) {
        super(component, item);

        this.statistics = Statistics.create()
            .category(datum, experience, level, skillPoints, attributePoints, enchantmentPoints, spentAttributePoints, spentEnchantmentPoints)
            .category(attribute, efficiency, reach, miningLevel)
            .min(0.5, efficiency).min(2, reach)
            .max(3, miningLevel)
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
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), new EntityAttributeModifier(SAAttributes.reachUUID, "Tool modifier", this.attributeRelative(reach), EntityAttributeModifier.Operation.ADDITION));
        }

        return modifiers;
    }

    @Override
    public List<StatisticEntry> screenAttributes() {
        List<StatisticEntry> entries = new ReferenceArrayList<>();

        entries.add(new StatisticEntry(this.statistic(efficiency), new Translation("%s%s: %s", Translations.toolEfficiencyFormat, Translations.toolEfficiencyName, this.formatStatistic(efficiency))));
        entries.add(new StatisticEntry(this.statistic(miningLevel), new Translation("%s%s: %s (%s)", Translations.miningLevelFormat, Translations.miningLevelName, this.formatStatistic(miningLevel), this.miningLevelName())));
        entries.add(new StatisticEntry(this.statistic(reach), new Translation("%s%s: %s", Translations.reachFormat, Translations.attackRangeName, this.formatStatistic(reach))));

        return entries;
    }

    @Override
    public List<Text> tooltip() {
        NumberFormat FORMAT = DecimalFormat.getInstance();
        List<Text> tooltip = new ArrayList<>(5);

        tooltip.add(new Translation(" %s%s %s", Translations.reachFormat, FORMAT.format(this.attribute(reach)), Translations.attackRangeName));
        tooltip.add(new Translation(" %s%s %s", Translations.toolEfficiencyFormat, FORMAT.format(this.attribute(efficiency)), Translations.toolEfficiencyName));
        tooltip.add(new Translation(" %s%s %s", Translations.miningLevelFormat, FORMAT.format(this.attribute(miningLevel)), Translations.miningLevelName));

        tooltip.add(LiteralText.EMPTY);
        tooltip.add(LiteralText.EMPTY);

        return tooltip;
    }

    @Override
    public double increase(StatisticType statistic, int points) {
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
