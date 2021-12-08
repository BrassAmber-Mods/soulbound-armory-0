package soulboundarmory.component.soulbound.item.weapon;

import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraftforge.common.ForgeMod;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.client.gui.screen.StatisticEntry;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.EnchantmentStorage;
import soulboundarmory.component.statistics.SkillStorage;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.component.statistics.Statistics;
import soulboundarmory.entity.Attributes;
import soulboundarmory.registry.Skills;
import soulboundarmory.util.AttributeModifierIdentifiers;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;

public class DaggerComponent extends WeaponComponent<DaggerComponent> {
    public DaggerComponent(SoulboundComponent component, Item item) {
        super(component, item);
    }

    public static DaggerComponent get(Entity entity) {
        return Components.weapon.of(entity).item(ItemComponentType.dagger);
    }

    @Override
    public Text name() {
        return Translations.guiDagger;
    }

    @Override
    public List<StatisticEntry> screenAttributes() {
        var critical = this.statistic(StatisticType.criticalStrikeRate);

        return List.of(
            new StatisticEntry(this.statistic(StatisticType.attackSpeed), Translations.guiAttackSpeed.format(this.formatStatistic(StatisticType.attackSpeed))),
            new StatisticEntry(this.statistic(StatisticType.attackDamage), Translations.guiAttackDamage.format(this.formatStatistic(StatisticType.attackDamage))),
            new StatisticEntry(critical, Translations.guiCriticalStrikeRate.format(statisticFormat.format(critical.doubleValue() * 100))),
            new StatisticEntry(this.statistic(StatisticType.efficiency), Translations.guiWeaponEfficiency.format(this.formatStatistic(StatisticType.efficiency)))
        );
    }

    @Override
    public List<Text> tooltip() {
        var format = DecimalFormat.getInstance();
        var tooltip = new ArrayList<>(List.of(
            Translations.tooltipAttackSpeed.format(format.format(this.doubleValue(StatisticType.attackSpeed))),
            Translations.tooltipAttackDamage.format(format.format(this.attributeTotal(StatisticType.attackDamage))),
            LiteralText.EMPTY,
            LiteralText.EMPTY
        ));

        if (this.doubleValue(StatisticType.criticalStrikeRate) > 0) {
            tooltip.add(Translations.tooltipCriticalStrikeRate.format(format.format(this.doubleValue(StatisticType.criticalStrikeRate) * 100)));
        }

        if (this.doubleValue(StatisticType.efficiency) > 0) {
            tooltip.add(Translations.tooltipToolEfficiency.format(format.format(this.doubleValue(StatisticType.efficiency))));
        }

        return tooltip;
    }

    @Override
    public Item consumableItem() {
        return Items.STONE_SWORD;
    }

    @Override
    public double increase(StatisticType statistic) {
        if (statistic == StatisticType.attackSpeed) return 0.04;
        if (statistic == StatisticType.attackDamage) return 0.05;
        if (statistic == StatisticType.criticalStrikeRate) return 0.02;
        if (statistic == StatisticType.efficiency) return 0.06;

        return 0;
    }

    @Override
    public ItemComponentType<DaggerComponent> type() {
        return ItemComponentType.dagger;
    }

    @Override
    public void attributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, this.weaponModifier(AttributeModifierIdentifiers.ItemAccess.attackSpeedModifier, StatisticType.attackSpeed));
            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, this.weaponModifier(AttributeModifierIdentifiers.ItemAccess.attackDamageModifier, StatisticType.attackDamage));
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), this.weaponModifier(Attributes.attackRange, StatisticType.attackRange));
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), this.weaponModifier(Attributes.reach, StatisticType.reach));
        }
    }

    @Override
    protected Statistics newStatistics() {
        return Statistics.builder()
            .category(Category.datum, StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints, StatisticType.spentAttributePoints, StatisticType.spentEnchantmentPoints)
            .category(Category.attribute, StatisticType.attackSpeed, StatisticType.attackDamage, StatisticType.criticalStrikeRate, StatisticType.efficiency, StatisticType.attackRange, StatisticType.reach)
            .min(2, StatisticType.attackSpeed, StatisticType.attackDamage, StatisticType.reach)
            .max(1, StatisticType.criticalStrikeRate).build();
    }

    @Override
    protected EnchantmentStorage newEnchantments() {
        return new EnchantmentStorage(enchantment -> {
            var name = enchantment.getTranslationKey().toLowerCase();

            return enchantment.isAcceptableItem(this.itemStack)
                && !Arrays.asList(UNBREAKING, VANISHING_CURSE).contains(enchantment)
                && (enchantment == SoulboundArmory.impact || !name.contains("soulbound"))
                && !name.contains("holding")
                && !name.contains("mending");
        });
    }

    @Override
    protected SkillStorage newSkills() {
        return new SkillStorage(Skills.nourishment, Skills.throwing, Skills.shadowClone, Skills.returning, Skills.sneakReturn);
    }
}
