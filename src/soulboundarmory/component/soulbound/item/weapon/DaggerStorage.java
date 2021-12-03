package soulboundarmory.component.soulbound.item.weapon;

import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.StorageType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.EnchantmentStorage;
import soulboundarmory.component.statistics.SkillStorage;
import soulboundarmory.component.statistics.Statistic;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.component.statistics.Statistics;
import soulboundarmory.client.gui.screen.StatisticEntry;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.entity.SAAttributes;
import soulboundarmory.registry.Skills;
import soulboundarmory.text.Translation;
import soulboundarmory.util.AttributeModifierIdentifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraftforge.common.ForgeMod;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;

public class DaggerStorage extends WeaponStorage<DaggerStorage> {
    public DaggerStorage(SoulboundComponent component, Item item) {
        super(component, item);

        this.statistics = Statistics.create()
            .category(Category.datum, StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints, StatisticType.spentAttributePoints, StatisticType.spentEnchantmentPoints)
            .category(Category.attribute, StatisticType.attackSpeed, StatisticType.attackDamage, StatisticType.criticalStrikeRate, StatisticType.efficiency, StatisticType.attackRange, StatisticType.reach)
            .min(2, StatisticType.attackSpeed, StatisticType.attackDamage, StatisticType.reach)
            .max(1, StatisticType.criticalStrikeRate).build();

        this.enchantments = new EnchantmentStorage(enchantment -> {
            var name = enchantment.getTranslationKey().toLowerCase();

            return enchantment.isAcceptableItem(this.itemStack)
                && !Arrays.asList(UNBREAKING, VANISHING_CURSE).contains(enchantment)
                && (enchantment == SoulboundArmory.impact || !name.contains("soulbound"))
                && !name.contains("holding")
                && !name.contains("mending");
        });

        this.skills = new SkillStorage(Skills.nourishment, Skills.throwing, Skills.shadowClone, Skills.returning, Skills.sneakReturn);
    }

    public static DaggerStorage get(Entity entity) {
        return Components.weapon.of(entity).storage(StorageType.dagger);
    }

    @Override
    public Text name() {
        return Translations.soulboundDagger;
    }

    @Override
    public List<StatisticEntry> screenAttributes() {
        var critical = this.statistic(StatisticType.criticalStrikeRate);

        return List.of(
            new StatisticEntry(this.statistic(StatisticType.attackSpeed), new Translation("%s%s: %s", Translations.attackSpeedFormat, Translations.attackSpeedName, this.formatStatistic(StatisticType.attackSpeed))),
            new StatisticEntry(this.statistic(StatisticType.attackDamage), new Translation("%s%s: %s", Translations.attackDamageFormat, Translations.attackDamageName, this.formatStatistic(StatisticType.attackDamage))),
            new StatisticEntry(critical, new Translation("%s%s: %s%%", Translations.criticalStrikeRateFormat, Translations.criticalStrikeRateName, statisticFormat.format(critical.doubleValue() * 100))),
            new StatisticEntry(this.statistic(StatisticType.efficiency), new Translation("%s%s: %s", Translations.weaponEfficiencyFormat, Translations.weaponEfficiencyName, this.formatStatistic(StatisticType.efficiency)))
        );
    }

    @Override
    public List<Text> tooltip() {
        var format = DecimalFormat.getInstance();
        var tooltip = new ArrayList<Text>(List.of(
            new Translation(" %s%s %s", format.format(this.attribute(StatisticType.attackSpeed)), Translations.attackSpeedName).styled(style -> style.withFormatting(Formatting.byCode(Translations.attackSpeedFormat.getKey().charAt(1)))),
            new Translation(" %s%s %s", Translations.attackDamageFormat, format.format(this.attributeTotal(StatisticType.attackDamage)), Translations.attackDamageName),
            LiteralText.EMPTY,
            LiteralText.EMPTY
        ));


        if (this.attribute(StatisticType.criticalStrikeRate) > 0) {
            tooltip.add(Text.of(String.format(" %s%s%% %s", Translations.criticalStrikeRateFormat, format.format(this.attribute(StatisticType.criticalStrikeRate) * 100), Translations.criticalStrikeRateName)));
        }

        if (this.attribute(StatisticType.efficiency) > 0) {
            tooltip.add(Text.of(String.format(" %s%s %s", Translations.toolEfficiencyFormat, format.format(this.attribute(StatisticType.efficiency)), Translations.toolEfficiencyName)));
        }

        return tooltip;
    }

    @Override
    public Item consumableItem() {
        return Items.STONE_SWORD;
    }

    @Override
    public double increase(StatisticType statistic, int points) {
        if (statistic == StatisticType.attackSpeed) return 0.04;
        if (statistic == StatisticType.attackDamage) return 0.05;
        if (statistic == StatisticType.criticalStrikeRate) return 0.02;
        if (statistic == StatisticType.efficiency) return 0.06;

        return 0;
    }

    @Override
    public StorageType<DaggerStorage> type() {
        return StorageType.dagger;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(AttributeModifierIdentifiers.ItemAccess.attackSpeedModifier, "Weapon modifier", this.attributeRelative(StatisticType.attackSpeed), EntityAttributeModifier.Operation.ADDITION));
            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(AttributeModifierIdentifiers.ItemAccess.attackDamageModifier, "Weapon modifier", this.attributeRelative(StatisticType.attackDamage), EntityAttributeModifier.Operation.ADDITION));
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), new EntityAttributeModifier(SAAttributes.attackRangeUUID, "Weapon modifier", this.attributeRelative(StatisticType.attackRange), EntityAttributeModifier.Operation.ADDITION));
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), new EntityAttributeModifier(SAAttributes.reachUUID, "Tool modifier", this.attributeRelative(StatisticType.reach), EntityAttributeModifier.Operation.ADDITION));
        }

        return modifiers;
    }
}
