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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import soulboundarmory.client.gui.screen.StatisticEntry;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.registry.Skills;
import soulboundarmory.registry.SoulboundItems;
import soulboundarmory.util.AttributeModifierIdentifiers;

public class SwordComponent extends WeaponComponent<SwordComponent> {
    protected int lightningCooldown;

    public SwordComponent(SoulboundComponent<?> component) {
        super(component);

        this.statistics
            .category(Category.datum, StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints)
            .category(Category.attribute, StatisticType.attackSpeed, StatisticType.attackDamage, StatisticType.criticalStrikeRate, StatisticType.efficiency, StatisticType.reach)
            .min(1.6, StatisticType.attackSpeed)
            .min(3, StatisticType.attackDamage)
            .min(3, StatisticType.reach)
            .max(1, StatisticType.criticalStrikeRate)
            .max(4, StatisticType.attackSpeed);

        this.enchantments.add(enchantment -> Stream.of("soulbound", "holding", "smelt").noneMatch(enchantment.getTranslationKey().toLowerCase()::contains));
        this.skills.add(Skills.circumspection, Skills.enderPull, Skills.precision, Skills.nourishment, Skills.summonLightning);
    }

    @Override
    public Text name() {
        return Translations.guiSword;
    }

    @Override
    public Item item() {
        return SoulboundItems.sword;
    }

    @Override
    public ItemComponentType<SwordComponent> type() {
        return ItemComponentType.sword;
    }

    public int lightningCooldown() {
        return this.lightningCooldown;
    }

    public void resetLightningCooldown() {
        if (!this.player.isCreative()) {
            this.lightningCooldown = (int) Math.round(96 / this.doubleValue(StatisticType.attackSpeed));
        }
    }

    @Override
    public void attributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, this.weaponModifier(AttributeModifierIdentifiers.ItemAccess.attackDamageModifier, StatisticType.attackDamage));
            modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, this.weaponModifier(AttributeModifierIdentifiers.ItemAccess.attackSpeedModifier, StatisticType.attackSpeed));
        }
    }

    @Override
    public List<StatisticEntry> screenAttributes() {
        return Arrays.asList(
            new StatisticEntry(this.statistic(StatisticType.attackSpeed), Translations.guiAttackSpeed.format(this.formatStatistic(StatisticType.attackSpeed))),
            new StatisticEntry(this.statistic(StatisticType.attackDamage), Translations.guiAttackDamage.format(this.formatStatistic(StatisticType.attackDamage))),
            new StatisticEntry(this.statistic(StatisticType.criticalStrikeRate), Translations.guiCriticalStrikeRate.format(this.formatStatistic(StatisticType.criticalStrikeRate))),
            new StatisticEntry(this.statistic(StatisticType.efficiency), Translations.guiWeaponEfficiency.format(this.formatStatistic(StatisticType.efficiency)))
        );
    }

    @Override
    public List<Text> tooltip() {
        var format = DecimalFormat.getInstance();

        return new ReferenceArrayList<>(List.of(
            Translations.tooltipAttackSpeed.translate(format.format(this.doubleValue(StatisticType.attackSpeed))),
            Translations.tooltipAttackDamage.translate(format.format(this.attributeTotal(StatisticType.attackDamage))),
            LiteralText.EMPTY,
            LiteralText.EMPTY,
            Translations.tooltipCriticalStrikeRate.translate(format.format(this.doubleValue(StatisticType.criticalStrikeRate) * 100)),
            Translations.tooltipToolEfficiency.translate(format.format(this.doubleValue(StatisticType.efficiency)))
        ));
    }

    @Override
    public double increase(StatisticType statistic) {
        if (statistic == StatisticType.attackSpeed) return 0.03;
        if (statistic == StatisticType.attackDamage) return 0.07;
        if (statistic == StatisticType.criticalStrikeRate) return 0.015;
        if (statistic == StatisticType.efficiency) return 0.04;

        return 0;
    }

    @Override
    public void tick() {
        if (!this.isClient()) {
            if (this.lightningCooldown > 0) {
                this.lightningCooldown--;
            }
        }
    }

    @Override
    public void serialize(NbtCompound tag) {
        super.serialize(tag);

        tag.putInt("lightningCooldown", this.lightningCooldown());
    }

    @Override
    public void deserialize(NbtCompound tag) {
        super.deserialize(tag);

        this.lightningCooldown = tag.getInt("lightningCooldown");
    }
}
