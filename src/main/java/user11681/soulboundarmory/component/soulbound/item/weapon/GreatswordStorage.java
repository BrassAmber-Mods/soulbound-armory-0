package user11681.soulboundarmory.component.soulbound.item.weapon;

import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
import org.jetbrains.annotations.NotNull;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.entity.EntityData;
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
import user11681.usersmanual.nbt.NBTUtil;
import user11681.usersmanual.text.StringifiedText;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;
import static net.minecraft.entity.EquipmentSlot.MAINHAND;
import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION;
import static user11681.soulboundarmory.SoulboundArmory.impact;
import static user11681.soulboundarmory.component.statistics.Category.attribute;
import static user11681.soulboundarmory.component.statistics.Category.datum;
import static user11681.soulboundarmory.component.statistics.StatisticType.attackDamage;
import static user11681.soulboundarmory.component.statistics.StatisticType.attackRange;
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

public class GreatswordStorage extends WeaponStorage<GreatswordStorage> {
    protected NbtCompound cannotFreeze;
    protected int leapDuration;
    protected double leapForce;

    public GreatswordStorage(final SoulboundComponent component, final Item item) {
        super(component, item);

        this.statistics = Statistics.create()
                .category(datum, experience, level, skillPoints, attributePoints, enchantmentPoints, spentAttributePoints, spentEnchantmentPoints)
                .category(attribute, attackSpeed, attackDamage, criticalStrikeProbability, efficiency, attackRange, reach)
                .min(0.8, attackSpeed).min(6, attackDamage).min(6, reach)
                .max(1, criticalStrikeProbability).build();
        this.enchantments = new EnchantmentStorage((final Enchantment enchantment) -> {
            final String name = enchantment.getName(1).asString().toLowerCase();

            return enchantment.isAcceptableItem(this.itemStack) && !CollectionUtil.hashSet(UNBREAKING, VANISHING_CURSE).contains(enchantment)
                    && (enchantment == impact || !name.contains("soulbound")) && !name.contains("holding")
                    && !name.contains("mending");
        });
        this.skills = new SkillStorage(Skills.NOURISHMENT, Skills.LEAPING, Skills.FREEZING);
        this.cannotFreeze = new NbtCompound();
    }

    public static GreatswordStorage get(final Entity entity) {
        return Components.weaponComponent.get(entity).getStorage(StorageType.greatsword);
    }

    @Override
    public Text getName() {
        return Translations.soulboundGreatsword;
    }

    @Override
    public StorageType<GreatswordStorage> getType() {
        return StorageType.greatsword;
    }

    @Override
    public Item getConsumableItem() {
        return Items.IRON_SWORD;
    }

    public double getLeapForce() {
        return this.leapForce;
    }

    public void setLeapForce(final double force) {
        this.resetLeapForce();
        this.leapForce = force;
    }

    public void resetLeapForce() {
        this.leapForce = 0;
        this.leapDuration = 0;

        NBTUtil.clear(this.cannotFreeze);
    }

    public int getLeapDuration() {
        return leapDuration;
    }

    public void setLeapDuration(final int ticks) {
        this.leapDuration = ticks;
    }

    public void freeze(final Entity entity, final int ticks, final double damage) {
        final EntityData component = Components.entityData.get(entity);
        final UUID id = entity.getUuid();
        final String key = id.toString();

        if (!this.cannotFreeze.contains(key) && component.canBeFrozen()) {
            component.freeze(this.player, ticks, (float) damage);

            this.cannotFreeze.putUuid(key, id);
        }
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(final Multimap<EntityAttribute, EntityAttributeModifier> modifiers, final EquipmentSlot slot) {
        if (slot == MAINHAND) {
            modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(AttributeModifierIdentifiers.ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", this.getAttributeRelative(attackSpeed), ADDITION));
            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(AttributeModifierIdentifiers.ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.getAttributeRelative(attackDamage), ADDITION));
            modifiers.put(ReachEntityAttributes.ATTACK_RANGE, new EntityAttributeModifier(SoulboundArmoryAttributes.ATTACK_RANGE_MODIFIER_UUID, "Weapon modifier", this.getAttributeRelative(attackRange), ADDITION));
            modifiers.put(ReachEntityAttributes.REACH, new EntityAttributeModifier(SoulboundArmoryAttributes.REACH_MODIFIER_UUID, "Weapon modifier", this.getAttributeRelative(reach), ADDITION));
        }

        return modifiers;
    }

    @Override
    public ArrayMap<Statistic, Text> getScreenAttributes() {
        final ArrayMap<Statistic, Text> entries = new OrderedArrayMap<>();

        entries.put(this.getStatistic(attackSpeed), new StringifiedText("%s%s: %s", Translations.attackSpeedFormat, Translations.attackSpeedName, this.formatStatistic(attackSpeed)));
        entries.put(this.getStatistic(attackDamage), new StringifiedText("%s%s: %s", Translations.attackDamageFormat, Translations.attackDamageName, this.formatStatistic(attackDamage)));
        entries.put(this.getStatistic(criticalStrikeProbability), new StringifiedText("%s%s: %s%%", Translations.criticalStrikeProbabilityFormat, Translations.criticalStrikeProbabilityName, this.formatStatistic(criticalStrikeProbability)));
        entries.put(this.getStatistic(efficiency), new StringifiedText("%s%s: %s", Translations.weaponEfficiencyFormat, Translations.weaponEfficiencyName, this.formatStatistic(efficiency)));

        return entries;
    }

    @Override
    public List<Text> getTooltip() {
        final NumberFormat format = DecimalFormat.getInstance();
        final List<Text> tooltip = new ArrayList<>();

        tooltip.add(new LiteralText(String.format(" %s%s %s", Translations.attackSpeedFormat, format.format(this.getAttribute(attackSpeed)), Translations.attackSpeedName)));
        tooltip.add(new LiteralText(String.format(" %s%s %s", Translations.attackDamageFormat, format.format(this.getAttributeTotal(attackDamage)), Translations.attackDamageName)));
        tooltip.add(new LiteralText(""));
        tooltip.add(new LiteralText(""));

        if (this.getAttribute(criticalStrikeProbability) > 0) {
            tooltip.add(new LiteralText(String.format(" %s%s%% %s", Translations.criticalStrikeProbabilityFormat, format.format(this.getAttribute(criticalStrikeProbability) * 100), Translations.criticalStrikeProbabilityName)));
        }

        if (this.getAttribute(efficiency) > 0) {
            tooltip.add(new LiteralText(String.format(" %s%s %s", Translations.toolEfficiencyFormat, format.format(this.getAttribute(efficiency)), Translations.toolEfficiencyName)));
        }

        return tooltip;
    }

    @Override
    public double getIncrease(final StatisticType statistic, final int points) {
        return statistic == attackSpeed
                ? 0.02
                : statistic == attackDamage
                ? 0.1
                : statistic == criticalStrikeProbability
                ? 0.01
                : statistic == efficiency
                ? 0.02
                : 0;
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
    public void fromTag(final NbtCompound tag) {
        super.fromTag(tag);

        this.cannotFreeze = tag.getCompound("cannotFreeze");
    }

        @Override
    public @NotNull NbtCompound toTag(NbtCompound tag) {
        tag = super.toTag(tag);

        tag.putInt("leapDuration", this.getLeapDuration());
        tag.putDouble("leapForce", this.getLeapForce());
        tag.put("cannotFreeze", this.cannotFreeze);

        return tag;
    }
}
