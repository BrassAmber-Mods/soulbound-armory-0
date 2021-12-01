package soulboundarmory.component.soulbound.item.weapon;

import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.component.Components;
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
import soulboundarmory.util.AttributeModifierIdentifiers;
import soulboundarmory.util.Util;
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
import net.minecraftforge.common.ForgeMod;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;

public class GreatswordStorage extends WeaponStorage<GreatswordStorage> {
    protected CompoundNBT cannotFreeze;
    public int leapDuration;
    public double leapForce;

    public GreatswordStorage(SoulboundComponent component, Item item) {
        super(component, item);

        this.statistics = Statistics.create()
            .category(Category.datum, StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints, StatisticType.spentAttributePoints, StatisticType.spentEnchantmentPoints)
            .category(Category.attribute, StatisticType.attackSpeed, StatisticType.attackDamage, StatisticType.criticalStrikeRate, StatisticType.efficiency, StatisticType.attackRange, StatisticType.reach)
            .min(0.8, StatisticType.attackSpeed).min(6, StatisticType.attackDamage).min(6, StatisticType.reach)
            .max(1, StatisticType.criticalStrikeRate).build();

        this.enchantments = new EnchantmentStorage(enchantment -> {
            var name = enchantment.getDescriptionId().toLowerCase(Locale.ROOT);

            return enchantment.canEnchant(this.itemStack) && !Util.contains(enchantment, UNBREAKING, VANISHING_CURSE)
                && (enchantment == SoulboundArmory.impact || !name.contains("soulbound")) && !name.contains("holding")
                && !name.contains("mending");
        });

        this.skills = new SkillStorage(Skills.nourishment, Skills.leaping, Skills.freezing);
        this.cannotFreeze = new CompoundNBT();
    }

    public static GreatswordStorage get(Entity entity) {
        return Components.weapon.of(entity).storage(StorageType.greatsword);
    }

    @Override
    public ITextComponent getName() {
        return Translations.soulboundGreatsword;
    }

    @Override
    public StorageType<GreatswordStorage> type() {
        return StorageType.greatsword;
    }

    @Override
    public Item getConsumableItem() {
        return Items.IRON_SWORD;
    }

    public double leapForce() {
        return this.leapForce;
    }

    public void leapForce(double force) {
        this.resetLeapForce();
        this.leapForce = force;
    }

    public void resetLeapForce() {
        this.leapForce = 0;
        this.leapDuration = 0;
        this.cannotFreeze = new CompoundNBT();
    }

    public int leapDuration() {
        return this.leapDuration;
    }

    public void leapDuration(int ticks) {
        this.leapDuration = ticks;
    }

    public void freeze(Entity entity, int ticks, double damage) {
        var component = Components.entityData.of(entity);
        var id = entity.getUUID();
        var key = id.toString();

        if (!this.cannotFreeze.contains(key) && component.canBeFrozen()) {
            component.freeze(this.player, ticks, (float) damage);

            this.cannotFreeze.putUUID(key, id);
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> attributeModifiers(Multimap<Attribute, AttributeModifier> modifiers, EquipmentSlotType slot) {
        if (slot == EquipmentSlotType.MAINHAND) {
            modifiers.put(Attributes.ATTACK_SPEED, new AttributeModifier(AttributeModifierIdentifiers.ItemAccess.attackSpeedModifier, "Weapon modifier", this.attributeRelative(StatisticType.attackSpeed), AttributeModifier.Operation.ADDITION));
            modifiers.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(AttributeModifierIdentifiers.ItemAccess.attackDamageModifier, "Weapon modifier", this.attributeRelative(StatisticType.attackDamage), AttributeModifier.Operation.ADDITION));
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(SAAttributes.attackRangeUUID, "Weapon modifier", this.attributeRelative(StatisticType.attackRange), AttributeModifier.Operation.ADDITION));
//            modifiers.put(ReachAttributes.REACH, new AttributeModifier(SAAttributes.reachUUID, "Weapon modifier", this.getAttributeRelative(reach), ADDITION));
        }

        return modifiers;
    }

    @Override
    public List<StatisticEntry> screenAttributes() {
        return List.of(
            new StatisticEntry(this.statistic(StatisticType.attackSpeed), Translation.of("%s%s: %s", Translations.attackSpeedFormat, Translations.attackSpeedName, this.formatStatistic(StatisticType.attackSpeed))),
            new StatisticEntry(this.statistic(StatisticType.attackDamage), Translation.of("%s%s: %s", Translations.attackDamageFormat, Translations.attackDamageName, this.formatStatistic(StatisticType.attackDamage))),
            new StatisticEntry(this.statistic(StatisticType.criticalStrikeRate), Translation.of("%s%s: %s%%", Translations.criticalStrikeRateFormat, Translations.criticalStrikeRateName, this.formatStatistic(StatisticType.criticalStrikeRate))),
            new StatisticEntry(this.statistic(StatisticType.efficiency), Translation.of("%s%s: %s", Translations.weaponEfficiencyFormat, Translations.weaponEfficiencyName, this.formatStatistic(StatisticType.efficiency)))
        );
    }

    @Override
    public List<ITextComponent> tooltip() {
        var format = DecimalFormat.getInstance();
        var tooltip = new ArrayList<>(List.of(
            new StringTextComponent(String.format(" %s%s %s", Translations.attackSpeedFormat, format.format(this.attribute(StatisticType.attackSpeed)), Translations.attackSpeedName)),
            new StringTextComponent(String.format(" %s%s %s", Translations.attackDamageFormat, format.format(this.attributeTotal(StatisticType.attackDamage)), Translations.attackDamageName)),
            StringTextComponent.EMPTY,
            StringTextComponent.EMPTY
        ));

        if (this.attribute(StatisticType.criticalStrikeRate) > 0) {
            tooltip.add(new StringTextComponent(String.format(" %s%s%% %s", Translations.criticalStrikeRateFormat, format.format(this.attribute(StatisticType.criticalStrikeRate) * 100), Translations.criticalStrikeRateName)));
        }

        if (this.attribute(StatisticType.efficiency) > 0) {
            tooltip.add(new StringTextComponent(String.format(" %s%s %s", Translations.toolEfficiencyFormat, format.format(this.attribute(StatisticType.efficiency)), Translations.toolEfficiencyName)));
        }

        return tooltip;
    }

    @Override
    public double increase(StatisticType statistic, int points) {
        if (statistic == StatisticType.attackSpeed) return 0.02;
        if (statistic == StatisticType.attackDamage) return 0.1;
        if (statistic == StatisticType.criticalStrikeRate) return 0.01;
        if (statistic == StatisticType.efficiency) return 0.02;

        return 0;
    }

    @Override
    public void tick() {
        if (this.leapDuration > 0) {
            if (--this.leapDuration == 0) {
                this.resetLeapForce();
            }
        }
    }

    @Override
    public void serializeNBT(CompoundNBT tag) {
        super.serializeNBT(tag);

        tag.putInt("leapDuration", this.leapDuration());
        tag.putDouble("leapForce", this.leapForce());
        tag.put("cannotFreeze", this.cannotFreeze);
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        super.deserializeNBT(tag);

        this.cannotFreeze = tag.getCompound("cannotFreeze");
    }
}
