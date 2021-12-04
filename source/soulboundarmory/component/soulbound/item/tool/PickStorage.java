package soulboundarmory.component.soulbound.item.tool;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraftforge.common.ForgeMod;
import soulboundarmory.client.gui.screen.StatisticEntry;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.StorageType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.EnchantmentStorage;
import soulboundarmory.component.statistics.SkillStorage;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.component.statistics.Statistics;
import soulboundarmory.entity.SAAttributes;
import soulboundarmory.registry.Skills;

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
            var name = enchantment.getName(1).getString().toLowerCase();

            return enchantment.isAcceptableItem(this.itemStack)
                && !Arrays.asList(UNBREAKING, VANISHING_CURSE, MENDING).contains(enchantment)
                && !Stream.of("soulbound", "holding", "smelt").map(name::contains).reduce(false, (contains, value) -> value || contains);
        });

        this.skills = new SkillStorage(Skills.enderPull, Skills.ambidexterity);
    }

    @Override
    public Item consumableItem() {
        return Items.WOODEN_PICKAXE;
    }

    @Override
    public Text name() {
        return Translations.guiPick;
    }

    @Override
    public StorageType<PickStorage> type() {
        return StorageType.pick;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers(EquipmentSlot slot) {
        var modifiers = HashMultimap.<EntityAttribute, EntityAttributeModifier>create();

        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), new EntityAttributeModifier(SAAttributes.reachUUID, "Tool modifier", this.attributeRelative(StatisticType.reach), EntityAttributeModifier.Operation.ADDITION));
        }

        return modifiers;
    }

    @Override
    public List<StatisticEntry> screenAttributes() {
        return List.of(
            new StatisticEntry(this.statistic(StatisticType.efficiency), Translations.guiToolEfficiency.format(this.formatStatistic(StatisticType.efficiency))),
            new StatisticEntry(this.statistic(StatisticType.miningLevel), Translations.guiMiningLevel.format(this.formatStatistic(StatisticType.miningLevel), this.miningLevelName())),
            new StatisticEntry(this.statistic(StatisticType.reach), Translations.guiReach.format(this.formatStatistic(StatisticType.reach)))
        );
    }

    @Override
    public List<Text> tooltip() {
        var format = DecimalFormat.getInstance();

        return List.of(
            Translations.tooltipReach.format(format.format(this.doubleValue(StatisticType.reach))),
            Translations.tooltipToolEfficiency.format(format.format(this.doubleValue(StatisticType.efficiency))),
            Translations.tooltipMiningLevel.format(format.format(this.doubleValue(StatisticType.miningLevel))),
            LiteralText.EMPTY,
            LiteralText.EMPTY
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
