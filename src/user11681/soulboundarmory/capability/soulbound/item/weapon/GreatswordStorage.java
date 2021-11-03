package user11681.soulboundarmory.capability.soulbound.item.weapon;

import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
import net.minecraftforge.common.ForgeMod;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.capability.entity.EntityData;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.capability.statistics.EnchantmentStorage;
import user11681.soulboundarmory.capability.statistics.SkillStorage;
import user11681.soulboundarmory.capability.statistics.StatisticType;
import user11681.soulboundarmory.capability.statistics.Statistics;
import user11681.soulboundarmory.client.gui.screen.StatisticEntry;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.entity.SAAttributes;
import user11681.soulboundarmory.registry.Skills;
import user11681.soulboundarmory.text.Translation;
import user11681.soulboundarmory.util.AttributeModifierIdentifiers;
import user11681.soulboundarmory.util.Util;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;
import static user11681.soulboundarmory.SoulboundArmory.impact;
import static user11681.soulboundarmory.capability.statistics.Category.attribute;
import static user11681.soulboundarmory.capability.statistics.Category.datum;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attackDamage;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attackRange;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attackSpeed;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attributePoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.criticalStrikeRate;
import static user11681.soulboundarmory.capability.statistics.StatisticType.efficiency;
import static user11681.soulboundarmory.capability.statistics.StatisticType.enchantmentPoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.experience;
import static user11681.soulboundarmory.capability.statistics.StatisticType.level;
import static user11681.soulboundarmory.capability.statistics.StatisticType.reach;
import static user11681.soulboundarmory.capability.statistics.StatisticType.skillPoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.spentAttributePoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.spentEnchantmentPoints;

public class GreatswordStorage extends WeaponStorage<GreatswordStorage> {
    protected NbtCompound cannotFreeze;
    public int leapDuration;
    public double leapForce;

    public GreatswordStorage(SoulboundCapability component, Item item) {
        super(component, item);

        this.statistics = Statistics.create()
            .category(datum, experience, level, skillPoints, attributePoints, enchantmentPoints, spentAttributePoints, spentEnchantmentPoints)
            .category(attribute, attackSpeed, attackDamage, criticalStrikeRate, efficiency, attackRange, reach)
            .min(0.8, attackSpeed).min(6, attackDamage).min(6, reach)
            .max(1, criticalStrikeRate).build();
        this.enchantments = new EnchantmentStorage((Enchantment enchantment) -> {
            String name = enchantment.getName(1).asString().toLowerCase();

            return enchantment.isAcceptableItem(this.itemStack) && !Util.contains(enchantment, UNBREAKING, VANISHING_CURSE)
                && (enchantment == impact || !name.contains("soulbound")) && !name.contains("holding")
                && !name.contains("mending");
        });
        this.skills = new SkillStorage(Skills.nourishment, Skills.leaping, Skills.freezing);
        this.cannotFreeze = new NbtCompound();
    }

    public static GreatswordStorage get(Entity entity) {
        return Capabilities.weapon.get(entity).storage(StorageType.greatsword);
    }

    @Override
    public Text getName() {
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
        this.cannotFreeze = new NbtCompound();
    }

    public int leapDuration() {
        return leapDuration;
    }

    public void leapDuration(int ticks) {
        this.leapDuration = ticks;
    }

    public void freeze(Entity entity, int ticks, double damage) {
        EntityData component = Capabilities.entityData.get(entity);
        UUID id = entity.getUuid();
        String key = id.toString();

        if (!this.cannotFreeze.contains(key) && component.canBeFrozen()) {
            component.freeze(this.player, ticks, (float) damage);

            this.cannotFreeze.putUuid(key, id);
        }
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(AttributeModifierIdentifiers.attackSpeedModifier, "Weapon modifier", this.attributeRelative(attackSpeed), EntityAttributeModifier.Operation.ADDITION));
            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(AttributeModifierIdentifiers.attackDamageModifier, "Weapon modifier", this.attributeRelative(attackDamage), EntityAttributeModifier.Operation.ADDITION));
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), new EntityAttributeModifier(SAAttributes.attackRangeUUID, "Weapon modifier", this.attributeRelative(attackRange), EntityAttributeModifier.Operation.ADDITION));
//            modifiers.put(ReachAttributes.REACH, new EntityAttributeModifier(SAAttributes.reachUUID, "Weapon modifier", this.getAttributeRelative(reach), ADDITION));
        }

        return modifiers;
    }

    @Override
    public List<StatisticEntry> screenAttributes() {
         List<StatisticEntry> entries = new ReferenceArrayList<>();

        entries.add(new StatisticEntry(this.statistic(attackSpeed), new Translation("%s%s: %s", Translations.attackSpeedFormat, Translations.attackSpeedName, this.formatStatistic(attackSpeed))));
        entries.add(new StatisticEntry(this.statistic(attackDamage), new Translation("%s%s: %s", Translations.attackDamageFormat, Translations.attackDamageName, this.formatStatistic(attackDamage))));
        entries.add(new StatisticEntry(this.statistic(criticalStrikeRate), new Translation("%s%s: %s%%", Translations.criticalStrikeRateFormat, Translations.criticalStrikeRateName, this.formatStatistic(criticalStrikeRate))));
        entries.add(new StatisticEntry(this.statistic(efficiency), new Translation("%s%s: %s", Translations.weaponEfficiencyFormat, Translations.weaponEfficiencyName, this.formatStatistic(efficiency))));

        return entries;
    }

    @Override
    public List<Text> tooltip() {
         NumberFormat format = DecimalFormat.getInstance();
         List<Text> tooltip = new ArrayList<>();

        tooltip.add(new LiteralText(String.format(" %s%s %s", Translations.attackSpeedFormat, format.format(this.attribute(attackSpeed)), Translations.attackSpeedName)));
        tooltip.add(new LiteralText(String.format(" %s%s %s", Translations.attackDamageFormat, format.format(this.attributeTotal(attackDamage)), Translations.attackDamageName)));
        tooltip.add(new LiteralText(""));
        tooltip.add(new LiteralText(""));

        if (this.attribute(criticalStrikeRate) > 0) {
            tooltip.add(new LiteralText(String.format(" %s%s%% %s", Translations.criticalStrikeRateFormat, format.format(this.attribute(criticalStrikeRate) * 100), Translations.criticalStrikeRateName)));
        }

        if (this.attribute(efficiency) > 0) {
            tooltip.add(new LiteralText(String.format(" %s%s %s", Translations.toolEfficiencyFormat, format.format(this.attribute(efficiency)), Translations.toolEfficiencyName)));
        }

        return tooltip;
    }

    @Override
    public double increase(StatisticType statistic, int points) {
        if (statistic == attackSpeed) {
            return 0.02;
        }

        if (statistic == attackDamage) {
            return 0.1;
        }

        if (statistic == criticalStrikeRate) {
            return 0.01;
        }

        if (statistic == efficiency) {
            return 0.02;
        }

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
