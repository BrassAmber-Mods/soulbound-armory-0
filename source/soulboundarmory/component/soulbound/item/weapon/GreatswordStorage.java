package soulboundarmory.component.soulbound.item.weapon;

import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
import net.minecraftforge.common.ForgeMod;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.client.gui.screen.StatisticEntry;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.StorageType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.EnchantmentStorage;
import soulboundarmory.component.statistics.SkillStorage;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.component.statistics.Statistics;
import soulboundarmory.entity.SAAttributes;
import soulboundarmory.registry.Skills;
import soulboundarmory.util.AttributeModifierIdentifiers;
import soulboundarmory.util.Util;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;

public class GreatswordStorage extends WeaponStorage<GreatswordStorage> {
    protected NbtCompound cannotFreeze;
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
            var name = enchantment.getTranslationKey().toLowerCase(Locale.ROOT);

            return enchantment.isAcceptableItem(this.itemStack) && !Util.contains(enchantment, UNBREAKING, VANISHING_CURSE)
                && (enchantment == SoulboundArmory.impact || !name.contains("soulbound")) && !name.contains("holding")
                && !name.contains("mending");
        });

        this.skills = new SkillStorage(Skills.nourishment, Skills.leaping, Skills.freezing);
        this.cannotFreeze = new NbtCompound();
    }

    public static GreatswordStorage get(Entity entity) {
        return Components.weapon.of(entity).storage(StorageType.greatsword);
    }

    @Override
    public Text name() {
        return Translations.guiGreatsword;
    }

    @Override
    public StorageType<GreatswordStorage> type() {
        return StorageType.greatsword;
    }

    @Override
    public Item consumableItem() {
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
        this.cannotFreeze = new NbtCompound();
    }

    public int leapDuration() {
        return this.leapDuration;
    }

    public void leapDuration(int ticks) {
        this.leapDuration = ticks;
    }

    public void freeze(Entity entity, int ticks, double damage) {
        var component = Components.entityData.of(entity);
        var id = entity.getUuid();
        var key = id.toString();

        if (!this.cannotFreeze.contains(key) && component.canBeFrozen()) {
            component.freeze(this.player, ticks, (float) damage);

            this.cannotFreeze.putUuid(key, id);
        }
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(AttributeModifierIdentifiers.ItemAccess.attackSpeedModifier, "Weapon modifier", this.attributeRelative(StatisticType.attackSpeed), EntityAttributeModifier.Operation.ADDITION));
            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(AttributeModifierIdentifiers.ItemAccess.attackDamageModifier, "Weapon modifier", this.attributeRelative(StatisticType.attackDamage), EntityAttributeModifier.Operation.ADDITION));
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), new EntityAttributeModifier(SAAttributes.attackRangeUUID, "Weapon modifier", this.attributeRelative(StatisticType.attackRange), EntityAttributeModifier.Operation.ADDITION));
//            modifiers.put(ReachAttributes.REACH, new EntityAttributeModifier(SAAttributes.reachUUID, "Weapon modifier", this.getAttributeRelative(reach), ADDITION));
        }

        return modifiers;
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
    public double increase(StatisticType statistic) {
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
    public void serializeNBT(NbtCompound tag) {
        super.serializeNBT(tag);

        tag.putInt("leapDuration", this.leapDuration());
        tag.putDouble("leapForce", this.leapForce());
        tag.put("cannotFreeze", this.cannotFreeze);
    }

    @Override
    public void deserializeNBT(NbtCompound tag) {
        super.deserializeNBT(tag);

        this.cannotFreeze = tag.getCompound("cannotFreeze");
    }
}
