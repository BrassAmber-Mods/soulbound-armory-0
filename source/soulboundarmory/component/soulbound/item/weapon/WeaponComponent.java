package soulboundarmory.component.soulbound.item.weapon;

import java.util.List;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.player.MasterComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.config.Configuration;
import soulboundarmory.util.EntityUtil;
import soulboundarmory.util.Util;

public abstract class WeaponComponent<T extends ItemComponent<T>> extends ItemComponent<T> {
    protected double criticalHitProgress;

    public WeaponComponent(MasterComponent<?> component) {
        super(component);

        this.statistics.statistics(StatisticType.criticalHitRate);
    }

    /**
     @return true if the hit is critical
     */
    public boolean hit() {
        this.criticalHitProgress += this.doubleValue(StatisticType.criticalHitRate);

        if (this.criticalHitProgress >= 1) {
            this.criticalHitProgress--;

            return true;
        }

        return false;
    }

    @Override
    public Item consumableItem() {
        return Items.WOODEN_SWORD;
    }

    @Override
    public int levelXP(int level) {
        return this.canLevelUp()
            ? Configuration.initialWeaponXP + 3 * (int) Math.round(Math.pow(level, 1.65))
            : -1;
    }

    @Override
    public void killed(LivingEntity entity) {
        if (this.isServer()) {
            var damage = EntityUtil.attribute(entity, EntityAttributes.GENERIC_ATTACK_DAMAGE);
            var speed = EntityUtil.attribute(entity, EntityAttributes.GENERIC_ATTACK_SPEED);
            var difficulty = this.player.world.getDifficulty().getId();

            var xp = entity.getMaxHealth()
                * (difficulty == 0 ? Configuration.Multipliers.peacefulMultiplier : difficulty) * Configuration.Multipliers.difficultyMultiplier
                * (1 + EntityUtil.attribute(entity, EntityAttributes.GENERIC_ARMOR) * Configuration.Multipliers.armorMultiplier)
                * (damage <= 0 ? Configuration.Multipliers.passiveMultiplier : 1 + damage * Configuration.Multipliers.attackDamageMultiplier)
                * (1 + speed * Configuration.Multipliers.attackSpeedMultiplier);

            if (EntityUtil.isBoss(entity)) {
                xp *= Configuration.Multipliers.bossMultiplier;
            }

            if (this.player.world.getServer().isHardcore()) {
                xp *= Configuration.Multipliers.hardcoreMultiplier;
            }

            if (damage > 0 && entity.isBaby()) {
                xp *= Configuration.Multipliers.babyMultiplier;
            }

            this.add(StatisticType.experience, Math.round(xp));
        }
    }

    @Override
    public double attributeTotal(StatisticType attribute) {
        if (attribute == StatisticType.efficiency && this.statistic(StatisticType.efficiency).min() == 0) {
            return this.doubleValue(attribute) == 0 ? 0 : super.attributeTotal(attribute) - this.increase(attribute);
        }

        return super.attributeTotal(attribute);
    }

    @Override
    public List<StatisticType> screenAttributes() {
        return ReferenceArrayList.of(StatisticType.attackDamage, StatisticType.attackSpeed, StatisticType.criticalHitRate);
    }

    @Override
    public List<Text> tooltip() {
        var tooltip = Util.list(
            Translations.tooltipAttackDamage.translate(this.formatValue(StatisticType.attackDamage)),
            Translations.tooltipAttackSpeed.translate(this.formatValue(StatisticType.attackSpeed))
        );

        if (this.criticalHitRate() > 0) tooltip.add(Translations.tooltipCriticalHitRate.translate(this.formatValue(StatisticType.criticalHitRate)));
        if (this.efficiency() > 0) tooltip.add(Translations.tooltipEfficiency.translate(this.formatValue(StatisticType.efficiency)));

        return tooltip;
    }

    @Override
    public void serialize(NbtCompound tag) {
        super.serialize(tag);

        tag.putDouble("criticalHitProgress", this.criticalHitProgress);
    }

    @Override
    public void deserialize(NbtCompound tag) {
        super.deserialize(tag);

        this.criticalHitProgress = tag.getDouble("criticalHitProgress");
    }
}
