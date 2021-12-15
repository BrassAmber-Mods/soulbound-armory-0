package soulboundarmory.component.soulbound.item.weapon;

import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraftforge.common.ForgeMod;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.Statistic;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.entity.Attributes;
import soulboundarmory.registry.Skills;
import soulboundarmory.registry.SoulboundItems;
import soulboundarmory.util.AttributeModifierIdentifiers;
import soulboundarmory.util.Math2;
import soulboundarmory.util.Util;

public class GreatswordComponent extends WeaponComponent<GreatswordComponent> {
    public int leapDuration;

    protected NbtCompound cannotFreeze = new NbtCompound();
    protected float zenith;
    protected float leapForce;

    public GreatswordComponent(SoulboundComponent<?> component) {
        super(component);

        this.statistics
            .category(Category.datum, StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints)
            .category(Category.attribute, StatisticType.attackSpeed, StatisticType.attackDamage, StatisticType.criticalHitRate, StatisticType.efficiency, StatisticType.reach)
            .min(0.8, StatisticType.attackSpeed)
            .min(4, StatisticType.attackDamage)
            .min(6, StatisticType.reach)
            .max(1, StatisticType.criticalHitRate)
            .max(4, StatisticType.attackSpeed);

        this.enchantments.add(enchantment -> Stream.of("soulbound", "holding", "smelt").noneMatch(enchantment.getTranslationKey().toLowerCase()::contains));
        this.skills.add(Skills.circumspection, Skills.enderPull, Skills.precision, Skills.nourishment, Skills.leaping, Skills.freezing);
    }

    @Override
    public ItemComponentType<GreatswordComponent> type() {
        return ItemComponentType.greatsword;
    }

    @Override
    public Item item() {
        return SoulboundItems.greatsword;
    }

    @Override
    public Text name() {
        return Translations.guiGreatsword;
    }

    public float leapForce() {
        return this.leapForce;
    }

    public void leap(float force) {
        this.resetLeapForce();
        this.leapForce = force;
        this.zenith = (float) Math2.zenith(this.player);
    }

    public void resetLeapForce() {
        this.leapForce = 0;
        this.leapDuration = 0;
        this.zenith = 0;
        this.cannotFreeze = new NbtCompound();
    }

    public float zenith() {
        return this.zenith;
    }

    public void freeze(Entity entity, int ticks, double damage) {
        var component = Components.entityData.of(entity);
        var id = entity.getUuid();
        var key = id.toString();

        if (!this.cannotFreeze.contains(key) && component.canBeFrozen()) {
            component.freeze(this.player, this.leapForce, ticks, (float) damage);
            this.cannotFreeze.putUuid(key, id);
        }
    }

    @Override
    public void attributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, this.weaponModifier(AttributeModifierIdentifiers.ItemAccess.attackSpeedModifier, StatisticType.attackSpeed));
            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, this.weaponModifier(AttributeModifierIdentifiers.ItemAccess.attackDamageModifier, StatisticType.attackDamage));
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), this.weaponModifier(Attributes.reach, StatisticType.reach));
        }
    }

    @Override
    public Map<Statistic, Text> screenAttributes() {
        return Util.add(super.screenAttributes(), this.statisticEntry(StatisticType.efficiency, Translations.guiWeaponEfficiency));
    }

    @Override
    public List<Text> tooltip() {
        var format = DecimalFormat.getInstance();
        var tooltip = new ArrayList<>(List.of(
            Translations.tooltipAttackSpeed.translate(format.format(this.doubleValue(StatisticType.attackSpeed))),
            Translations.tooltipAttackDamage.translate(format.format(this.attributeTotal(StatisticType.attackDamage))),
            LiteralText.EMPTY,
            LiteralText.EMPTY
        ));

        if (this.doubleValue(StatisticType.criticalHitRate) > 0) {
            tooltip.add(Translations.tooltipCriticalHitRate.translate(format.format(this.doubleValue(StatisticType.criticalHitRate) * 100)));
        }

        if (this.doubleValue(StatisticType.efficiency) > 0) {
            tooltip.add(Translations.tooltipToolEfficiency.translate(format.format(this.doubleValue(StatisticType.efficiency))));
        }

        return tooltip;
    }

    @Override
    public double increase(StatisticType statistic) {
        if (statistic == StatisticType.attackSpeed) return 0.02;
        if (statistic == StatisticType.attackDamage) return 0.1;
        if (statistic == StatisticType.criticalHitRate) return 0.01;
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
    public void serialize(NbtCompound tag) {
        super.serialize(tag);

        tag.putInt("leapDuration", this.leapDuration);
        tag.putDouble("leapForce", this.leapForce());
        tag.put("cannotFreeze", this.cannotFreeze);
    }

    @Override
    public void deserialize(NbtCompound tag) {
        super.deserialize(tag);

        this.cannotFreeze = tag.getCompound("cannotFreeze");
    }
}
