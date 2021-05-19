package user11681.soulboundarmory.capability.soulbound.item.weapon;

import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.capability.statistics.EnchantmentStorage;
import user11681.soulboundarmory.capability.statistics.SkillStorage;
import user11681.soulboundarmory.capability.statistics.StatisticType;
import user11681.soulboundarmory.capability.statistics.Statistics;
import user11681.soulboundarmory.client.gui.screen.tab.StatisticEntry;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.entity.SAAttributes;
import user11681.soulboundarmory.registry.Skills;
import user11681.soulboundarmory.text.Translation;
import user11681.soulboundarmory.util.AttributeModifierIdentifiers;
import user11681.soulboundarmory.util.AttributeModifierOperations;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;
import static user11681.soulboundarmory.SoulboundArmory.impact;
import static user11681.soulboundarmory.capability.statistics.Category.attribute;
import static user11681.soulboundarmory.capability.statistics.Category.datum;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attackDamage;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attackSpeed;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attributePoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.criticalStrikeProbability;
import static user11681.soulboundarmory.capability.statistics.StatisticType.efficiency;
import static user11681.soulboundarmory.capability.statistics.StatisticType.enchantmentPoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.experience;
import static user11681.soulboundarmory.capability.statistics.StatisticType.level;
import static user11681.soulboundarmory.capability.statistics.StatisticType.reach;
import static user11681.soulboundarmory.capability.statistics.StatisticType.skillPoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.spentAttributePoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.spentEnchantmentPoints;

public class SwordStorage extends WeaponStorage<SwordStorage> {
    protected int lightningCooldown;

    public SwordStorage(SoulboundCapability component, final Item item) {
        super(component, item);

        this.statistics = Statistics.create()
            .category(datum, experience, level, skillPoints, attributePoints, enchantmentPoints, spentAttributePoints, spentEnchantmentPoints)
            .category(attribute, attackSpeed, attackDamage, criticalStrikeProbability, efficiency, reach)
            .min(1.6, attackSpeed).min(4, attackDamage).min(3, reach)
            .max(1, criticalStrikeProbability)
            .build();
        this.enchantments = new EnchantmentStorage((Enchantment enchantment) -> {
            final String name = enchantment.getFullname(1).getContents().toLowerCase();

            return enchantment.canEnchant(this.itemStack) && !Arrays.asList(UNBREAKING, VANISHING_CURSE).contains(enchantment)
                && (enchantment == impact || !name.contains("soulbound")) && !name.contains("holding")
                && !name.contains("mending");
        });
        this.skills = new SkillStorage(Skills.nourishment, Skills.summonLightning);
    }

    public static SwordStorage get(Entity entity) {
        return Capabilities.weapon.get(entity).storage(StorageType.sword);
    }

    @Override
    public ITextComponent getName() {
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
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(Multimap<Attribute, AttributeModifier> modifiers, EquipmentSlotType slot) {
        if (slot == EquipmentSlotType.MAINHAND) {
            final AttributeModifier.Operation addition = AttributeModifier.Operation.ADDITION;

            modifiers.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(AttributeModifierIdentifiers.ATTACK_DAMAGE_MODIFIER_ID,
                "Weapon modifier", this.getAttributeRelative(attackDamage), addition
            ));
            modifiers.put(Attributes.ATTACK_SPEED, new AttributeModifier(AttributeModifierIdentifiers.ATTACK_SPEED_MODIFIER_ID,
                "Weapon modifier", this.getAttributeRelative(attackSpeed), addition
            ));
            modifiers.put(SAAttributes.efficiency, new AttributeModifier(SAAttributes.efficiencyUUID,
                "Weapon modifier", this.getAttribute(efficiency), AttributeModifier.Operation.MULTIPLY_TOTAL
            ));
            modifiers.put(SAAttributes.criticalStrikeProbability, new AttributeModifier(SAAttributes.criticalStrikeRateUUID,
                "Weapon modifier", this.getAttribute(criticalStrikeProbability), AttributeModifierOperations.percentageAddition
            ));
        }

        return modifiers;
    }

    @Override
    public List<StatisticEntry> getScreenAttributes() {
        List<StatisticEntry> entries = new ReferenceArrayList<>();

        entries.add(new StatisticEntry(this.getStatistic(attackSpeed), new Translation("%s%s: %s", Translations.attackSpeedFormat, Translations.attackSpeedName, this.formatStatistic(attackSpeed)).withStyle(TextFormatting.GOLD)));
        entries.add(new StatisticEntry(this.getStatistic(attackDamage), new Translation("%s%s: %s", Translations.attackDamageFormat, Translations.attackDamageName, this.formatStatistic(attackDamage))));
        entries.add(new StatisticEntry(this.getStatistic(criticalStrikeProbability), new Translation("%s%s: %s%%", Translations.criticalStrikeProbabilityFormat, Translations.criticalStrikeProbabilityName, this.formatStatistic(criticalStrikeProbability))));
        entries.add(new StatisticEntry(this.getStatistic(efficiency), new Translation("%s%s: %s", Translations.weaponEfficiencyFormat, Translations.weaponEfficiencyName, this.formatStatistic(efficiency))));

        return entries;
    }

    @Override
    public List<ITextComponent> getTooltip() {
        final NumberFormat format = DecimalFormat.getInstance();
        final List<ITextComponent> tooltip = new ReferenceArrayList<>();

        tooltip.add(new StringTextComponent(String.format(" %s%s %s", Translations.attackSpeedFormat, format.format(this.getAttribute(attackSpeed)), Translations.attackSpeedName)));
        tooltip.add(new StringTextComponent(String.format(" %s%s %s", Translations.attackDamageFormat, format.format(this.getAttributeTotal(attackDamage)), Translations.attackDamageName)));

        tooltip.add(new StringTextComponent(""));
        tooltip.add(new StringTextComponent(""));

        tooltip.add(new StringTextComponent(String.format(" %s%s%% %s", Translations.criticalStrikeProbabilityFormat, format.format(this.getAttribute(criticalStrikeProbability) * 100), Translations.criticalStrikeProbabilityName)));
        tooltip.add(new StringTextComponent(String.format(" %s%s %s", Translations.toolEfficiencyFormat, format.format(this.getAttribute(efficiency)), Translations.toolEfficiencyName)));

        return tooltip;
    }

    @Override
    public Item getConsumableItem() {
        return Items.WOODEN_SWORD;
    }

    @Override
    public double getIncrease(StatisticType statistic, int points) {
        if (statistic == attackSpeed) {
            return 0.03;
        }

        if (statistic == attackDamage) {
            return 0.07;
        }

        if (statistic == criticalStrikeProbability) {
            return 0.015;
        }

        if (statistic == efficiency) {
            return 0.04;
        }

        return 0;
    }

    @Override
    public void tick() {
        if (!this.isClientSide) {
            if (this.lightningCooldown > 0) {
                this.lightningCooldown--;
            }
        }
    }

    @Override
    public void serializeNBT(CompoundNBT tag) {
        super.serializeNBT(tag);

        tag.putInt("lightningCooldown", this.getLightningCooldown());
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        super.deserializeNBT(tag);

        this.lightningCooldown = tag.getInt("lightningCooldown");
    }
}
