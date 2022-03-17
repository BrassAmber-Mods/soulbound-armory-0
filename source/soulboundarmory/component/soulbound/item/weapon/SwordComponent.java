package soulboundarmory.component.soulbound.item.weapon;

import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Statistic;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.skill.Skills;
import soulboundarmory.item.SoulboundItems;
import soulboundarmory.util.AttributeModifierIdentifiers;
import soulboundarmory.util.Util;

public class SwordComponent extends WeaponComponent<SwordComponent> {
    protected int lightningCooldown;

    public SwordComponent(SoulboundComponent<?> component) {
        super(component);

        this.statistics
            .statistics(StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints)
            .statistics(StatisticType.efficiency)
            .constant(3, StatisticType.reach)
            .min(1.6, StatisticType.attackSpeed)
            .min(3, StatisticType.attackDamage);

        this.enchantments.initialize(enchantment -> Stream.of("soulbound", "holding", "smelt").noneMatch(enchantment.getTranslationKey().toLowerCase()::contains));
        this.skills.add(Skills.circumspection, Skills.precision, Skills.nourishment, Skills.summonLightning);
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
            this.lightningCooldown = (int) Math.round(96 / this.attackSpeed());
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
    public Map<Statistic, Text> screenAttributes() {
        return Util.add(super.screenAttributes(), this.statisticEntry(StatisticType.efficiency, Translations.guiEfficiency));
    }

    @Override
    public double increase(StatisticType type) {
        if (type == StatisticType.attackSpeed) return 0.03;
        if (type == StatisticType.attackDamage) return 0.07;
        if (type == StatisticType.criticalHitRate) return 0.015;
        if (type == StatisticType.efficiency) return 0.04;

        return 0;
    }

    @Override
    public void tick() {
        if (this.isServer()) {
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
