package soulboundarmory.component.soulbound.item.tool;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import soulboundarmory.component.soulbound.item.StorageType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.EnchantmentStorage;
import soulboundarmory.component.statistics.SkillStorage;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.component.statistics.Statistics;
import soulboundarmory.client.gui.screen.StatisticEntry;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.entity.SAAttributes;
import soulboundarmory.registry.Skills;
import soulboundarmory.text.Translation;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeMod;

import static net.minecraft.enchantment.Enchantments.MENDING;
import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;

public class PickStorage extends ToolStorage<PickStorage> {
    public PickStorage(SoulboundComponent component, Item item) {
        super(component, item);

        this.statistics = Statistics.create()
            .category(Category.datum, StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints, StatisticType.spentAttributePoints, StatisticType.spentEnchantmentPoints)
            .category(Category.attribute, StatisticType.efficiency, StatisticType.reach, StatisticType.miningLevel)
            .min(0.5, StatisticType.efficiency).min(2, StatisticType.reach)
            .max(3, StatisticType.miningLevel)
            .build();

        this.enchantments = new EnchantmentStorage(enchantment -> {
            var name = enchantment.getFullname(1).getString().toLowerCase();

            return enchantment.canEnchant(this.itemStack)
                && !Arrays.asList(UNBREAKING, VANISHING_CURSE, MENDING).contains(enchantment)
                && !Stream.of("soulbound", "holding", "smelt").map(name::contains).reduce(false, (contains, value) -> value || contains);
        });

        this.skills = new SkillStorage(Skills.enderPull, Skills.ambidexterity);
    }

    @Override
    public Item getConsumableItem() {
        return Items.WOODEN_PICKAXE;
    }

    @Override
    public ITextComponent getName() {
        return Translations.soulboundPick;
    }

    @Override
    public StorageType<PickStorage> type() {
        return StorageType.pick;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> attributeModifiers(EquipmentSlotType slot) {
        var modifiers = HashMultimap.<Attribute, AttributeModifier>create();

        if (slot == EquipmentSlotType.MAINHAND) {
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(SAAttributes.reachUUID, "Tool modifier", this.attributeRelative(StatisticType.reach), AttributeModifier.Operation.ADDITION));
        }

        return modifiers;
    }

    @Override
    public List<StatisticEntry> screenAttributes() {
        return List.of(
            new StatisticEntry(this.statistic(StatisticType.efficiency), new Translation("%s%s: %s", Translations.toolEfficiencyFormat, Translations.toolEfficiencyName, this.formatStatistic(StatisticType.efficiency))),
            new StatisticEntry(this.statistic(StatisticType.miningLevel), new Translation("%s%s: %s (%s)", Translations.miningLevelFormat, Translations.miningLevelName, this.formatStatistic(StatisticType.miningLevel), this.miningLevelName())),
            new StatisticEntry(this.statistic(StatisticType.reach), new Translation("%s%s: %s", Translations.reachFormat, Translations.attackRangeName, this.formatStatistic(StatisticType.reach)))
        );
    }

    @Override
    public List<ITextComponent> tooltip() {
        var format = DecimalFormat.getInstance();

        return List.of(
            new Translation(" %s%s %s", Translations.reachFormat, format.format(this.attribute(StatisticType.reach)), Translations.attackRangeName),
            new Translation(" %s%s %s", Translations.toolEfficiencyFormat, format.format(this.attribute(StatisticType.efficiency)), Translations.toolEfficiencyName),
            new Translation(" %s%s %s", Translations.miningLevelFormat, format.format(this.attribute(StatisticType.miningLevel)), Translations.miningLevelName),
            StringTextComponent.EMPTY,
            StringTextComponent.EMPTY
        );
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
