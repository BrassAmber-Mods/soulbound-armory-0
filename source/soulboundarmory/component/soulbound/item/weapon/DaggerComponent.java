package soulboundarmory.component.soulbound.item.weapon;

import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraftforge.common.ForgeMod;
import soulboundarmory.client.gui.screen.StatisticEntry;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.entity.Attributes;
import soulboundarmory.registry.Skills;
import soulboundarmory.registry.SoulboundItems;
import soulboundarmory.util.AttributeModifierIdentifiers;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;

public class DaggerComponent extends WeaponComponent<DaggerComponent> {
    public DaggerComponent(SoulboundComponent<?> component) {
        super(component);

        this.statistics
            .category(Category.datum, StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints)
            .category(Category.attribute, StatisticType.attackSpeed, StatisticType.attackDamage, StatisticType.criticalStrikeRate, StatisticType.efficiency, StatisticType.attackRange, StatisticType.reach)
            .min(2, StatisticType.attackSpeed, StatisticType.attackDamage, StatisticType.reach)
            .max(1, StatisticType.criticalStrikeRate)
            .max(4, StatisticType.attackSpeed);

        this.enchantments.add(enchantment -> enchantment.type.isAcceptableItem(this.item())
            && !Arrays.asList(UNBREAKING, VANISHING_CURSE).contains(enchantment)
            && Stream.of("soulbound", "holding", "smelt").noneMatch(enchantment.getTranslationKey().toLowerCase()::contains)
        );

        this.skills.add(Skills.nourishment, Skills.throwing, Skills.shadowClone, Skills.returning, Skills.sneakReturn);
    }

    @Override
    public ItemComponentType<DaggerComponent> type() {
        return ItemComponentType.dagger;
    }

    @Override
    public Item item() {
        return SoulboundItems.dagger;
    }

    @Override
    public Text name() {
        return Translations.guiDagger;
    }

    @Override
    public List<StatisticEntry> screenAttributes() {
        return List.of(
            new StatisticEntry(this.statistic(StatisticType.attackSpeed), Translations.guiAttackSpeed.format(this.formatStatistic(StatisticType.attackSpeed))),
            new StatisticEntry(this.statistic(StatisticType.attackDamage), Translations.guiAttackDamage.format(this.formatStatistic(StatisticType.attackDamage))),
            new StatisticEntry(this.statistic(StatisticType.criticalStrikeRate), Translations.guiCriticalStrikeRate.format(this.formatStatistic(StatisticType.criticalStrikeRate))),
            new StatisticEntry(this.statistic(StatisticType.efficiency), Translations.guiWeaponEfficiency.format(this.formatStatistic(StatisticType.efficiency)))
        );
    }

    @Override
    public List<Text> tooltip() {
        var format = DecimalFormat.getInstance();
        var tooltip = new ReferenceArrayList<>(List.of(
            Translations.tooltipAttackSpeed.translate(format.format(this.doubleValue(StatisticType.attackSpeed))),
            Translations.tooltipAttackDamage.translate(format.format(this.attributeTotal(StatisticType.attackDamage))),
            LiteralText.EMPTY,
            LiteralText.EMPTY
        ));

        if (this.doubleValue(StatisticType.criticalStrikeRate) > 0) {
            tooltip.add(Translations.tooltipCriticalStrikeRate.translate(format.format(this.doubleValue(StatisticType.criticalStrikeRate) * 100)));
        }

        if (this.doubleValue(StatisticType.efficiency) > 0) {
            tooltip.add(Translations.tooltipToolEfficiency.translate(format.format(this.doubleValue(StatisticType.efficiency))));
        }

        return tooltip;
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
    public void attributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, this.weaponModifier(AttributeModifierIdentifiers.ItemAccess.attackSpeedModifier, StatisticType.attackSpeed));
            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, this.weaponModifier(AttributeModifierIdentifiers.ItemAccess.attackDamageModifier, StatisticType.attackDamage));
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), this.weaponModifier(Attributes.attackRange, StatisticType.attackRange));
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), this.weaponModifier(Attributes.reach, StatisticType.reach));
        }
    }
}
