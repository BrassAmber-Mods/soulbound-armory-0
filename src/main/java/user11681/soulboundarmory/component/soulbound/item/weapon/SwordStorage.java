package user11681.soulboundarmory.component.soulbound.item.weapon;

import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
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
import user11681.usersmanual.collections.ArrayMap;
import user11681.usersmanual.collections.CollectionUtil;
import user11681.usersmanual.collections.OrderedArrayMap;
import user11681.usersmanual.entity.AttributeModifierIdentifiers;
import user11681.usersmanual.entity.AttributeModifierOperations;
import user11681.usersmanual.text.StringifiedText;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;
import static net.minecraft.entity.EquipmentSlot.MAINHAND;
import static user11681.soulboundarmory.SoulboundArmory.impact;
import static user11681.soulboundarmory.component.statistics.Category.attribute;
import static user11681.soulboundarmory.component.statistics.Category.datum;
import static user11681.soulboundarmory.component.statistics.StatisticType.attackDamage;
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

public class SwordStorage extends WeaponStorage<SwordStorage> {
    protected int lightningCooldown;

    public SwordStorage(final SoulboundComponent component, final Item item) {
        super(component, item);

        this.statistics = Statistics.create()
                .category(datum, experience, level, skillPoints, attributePoints, enchantmentPoints, spentAttributePoints, spentEnchantmentPoints)
                .category(attribute, attackSpeed, attackDamage, criticalStrikeProbability, efficiency, reach)
                .min(1.6, attackSpeed).min(4, attackDamage).min(3, reach)
                .max(1, criticalStrikeProbability)
                .build();
        this.enchantments = new EnchantmentStorage((final Enchantment enchantment) -> {
            final String name = enchantment.getName(1).asString().toLowerCase();

            return enchantment.isAcceptableItem(this.itemStack) && !CollectionUtil.hashSet(UNBREAKING, VANISHING_CURSE).contains(enchantment)
                    && (enchantment == impact || !name.contains("soulbound")) && !name.contains("holding")
                    && !name.contains("mending");
        });
        this.skills = new SkillStorage(Skills.NOURISHMENT, Skills.SUMMON_LIGHTNING);
    }

    public static SwordStorage get(final Entity entity) {
        return Components.weaponComponent.get(entity).getStorage(StorageType.sword);
    }

    @Override
    public Text getName() {
        return Translations.soulboundSword;
    }

    @Override
    public StorageType<SwordStorage> getType() {
        return StorageType.sword;
    }

    public int getLightningCooldown() {
        return this.lightningCooldown;
    }

    public void resetLightningCooldown() {
        if (!this.player.isCreative()) {
            this.lightningCooldown = (int) Math.round(96 / this.getAttribute(attackSpeed));
        }
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(final Multimap<EntityAttribute, EntityAttributeModifier> modifiers, final EquipmentSlot slot) {
        if (slot == MAINHAND) {
            final EntityAttributeModifier.Operation addition = EntityAttributeModifier.Operation.ADDITION;

            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(AttributeModifierIdentifiers.ATTACK_DAMAGE_MODIFIER_ID,
                    "Weapon modifier", this.getAttributeRelative(attackDamage), addition
            ));
            modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(AttributeModifierIdentifiers.ATTACK_SPEED_MODIFIER_ID,
                    "Weapon modifier", this.getAttributeRelative(attackSpeed), addition
            ));
            modifiers.put(SoulboundArmoryAttributes.GENERIC_EFFICIENCY, new EntityAttributeModifier(SoulboundArmoryAttributes.EFFICIENCY_MODIFIER_ID,
                    "Weapon modifier", this.getAttribute(efficiency), EntityAttributeModifier.Operation.MULTIPLY_TOTAL
            ));
            modifiers.put(SoulboundArmoryAttributes.GENERIC_CRITICAL_STRIKE_PROBABILITY, new EntityAttributeModifier(SoulboundArmoryAttributes.CRITICAL_STRIKE_PROBABILITY_MODIFIER_ID,
                    "Weapon modifier", this.getAttribute(criticalStrikeProbability), AttributeModifierOperations.PERCENTAGE_ADDITION
            ));
        }

        return modifiers;
    }

    @Override
    public ArrayMap<Statistic, Text> getScreenAttributes() {
        final ArrayMap<Statistic, Text> entries = new OrderedArrayMap<>();

        entries.put(this.getStatistic(attackSpeed), new StringifiedText("%s%s: %s", Translations.attackSpeedFormat, Translations.attackSpeedName, this.formatStatistic(attackSpeed)).formatted(Formatting.GOLD));
        entries.put(this.getStatistic(attackDamage), new StringifiedText("%s%s: %s", Translations.attackDamageFormat, Translations.attackDamageName, this.formatStatistic(attackDamage)));
        entries.put(this.getStatistic(criticalStrikeProbability), new StringifiedText("%s%s: %s%%", Translations.criticalStrikeProbabilityFormat, Translations.criticalStrikeProbabilityName, this.formatStatistic(criticalStrikeProbability)));
        entries.put(this.getStatistic(efficiency), new StringifiedText("%s%s: %s", Translations.weaponEfficiencyFormat, Translations.weaponEfficiencyName, this.formatStatistic(efficiency)));

        return entries;
    }

    @Override
    public List<Text> getTooltip() {
        final NumberFormat format = DecimalFormat.getInstance();
        final List<Text> tooltip = new ArrayList<>();

        tooltip.add(new LiteralText(String.format(" %s%s %s", Translations.attackSpeedFormat, format.format(this.getAttribute(attackSpeed)), Translations.attackSpeedName.toString())));
        tooltip.add(new LiteralText(String.format(" %s%s %s", Translations.attackDamageFormat, format.format(this.getAttributeTotal(attackDamage)), Translations.attackDamageName.toString())));

        tooltip.add(new LiteralText(""));
        tooltip.add(new LiteralText(""));

        tooltip.add(new LiteralText(String.format(" %s%s%% %s", Translations.criticalStrikeProbabilityFormat, format.format(this.getAttribute(criticalStrikeProbability) * 100), Translations.criticalStrikeProbabilityName.toString())));
        tooltip.add(new LiteralText(String.format(" %s%s %s", Translations.toolEfficiencyFormat, format.format(this.getAttribute(efficiency)), Translations.toolEfficiencyName.toString())));

        return tooltip;
    }

    @Override
    public Item getConsumableItem() {
        return Items.WOODEN_SWORD;
    }

    @Override
    public double getIncrease(final StatisticType statistic, final int points) {
        return statistic == attackSpeed
                ? 0.03
                : statistic == attackDamage
                ? 0.07
                : statistic == criticalStrikeProbability
                ? 0.015
                : statistic == efficiency
                ? 0.04
                : 0;
    }

    @Override
    public void tick() {
        if (!this.isClient) {
            if (this.lightningCooldown > 0) {
                this.lightningCooldown--;
            }
        }
    }

    @Override
    public void fromTag(final NbtCompound tag) {
        super.fromTag(tag);

        this.lightningCooldown = tag.getInt("lightningCooldown");
    }

        @Override
    public @NotNull NbtCompound toTag(NbtCompound tag) {
        tag = super.toTag(tag);

        tag.putInt("lightningCooldown", this.getLightningCooldown());

        return tag;
    }
}
