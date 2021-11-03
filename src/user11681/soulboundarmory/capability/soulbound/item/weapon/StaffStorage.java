package user11681.soulboundarmory.capability.soulbound.item.weapon;

import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.capability.statistics.EnchantmentStorage;
import user11681.soulboundarmory.capability.statistics.SkillStorage;
import user11681.soulboundarmory.capability.statistics.StatisticType;
import user11681.soulboundarmory.capability.statistics.Statistics;
import user11681.soulboundarmory.client.gui.screen.StatisticEntry;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.registry.Skills;
import user11681.soulboundarmory.registry.SoulboundItems;
import user11681.soulboundarmory.text.Translation;
import user11681.soulboundarmory.util.AttributeModifierIdentifiers;
import user11681.soulboundarmory.util.Util;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;
import static user11681.soulboundarmory.SoulboundArmory.impact;
import static user11681.soulboundarmory.capability.statistics.Category.attribute;
import static user11681.soulboundarmory.capability.statistics.Category.datum;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attackDamage;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attackSpeed;
import static user11681.soulboundarmory.capability.statistics.StatisticType.attributePoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.criticalStrikeRate;
import static user11681.soulboundarmory.capability.statistics.StatisticType.enchantmentPoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.experience;
import static user11681.soulboundarmory.capability.statistics.StatisticType.level;
import static user11681.soulboundarmory.capability.statistics.StatisticType.skillPoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.spentAttributePoints;
import static user11681.soulboundarmory.capability.statistics.StatisticType.spentEnchantmentPoints;

public class StaffStorage extends WeaponStorage<StaffStorage> {
    protected int fireballCooldown;
    protected int spell;

    public StaffStorage(SoulboundCapability component, Item item) {
        super(component, item);
        this.statistics = Statistics.create()
                .category(datum, experience, level, skillPoints, attributePoints, enchantmentPoints, spentAttributePoints, spentEnchantmentPoints)
                .category(attribute, attackSpeed, attackDamage, criticalStrikeRate)
                .min(0.48, attackSpeed).min(8, attackDamage)
                .max(1, criticalStrikeRate).build();
        this.enchantments = new EnchantmentStorage((Enchantment enchantment) -> {
             String name = enchantment.getName(1).asString().toLowerCase();

            return enchantment.isAcceptableItem(this.itemStack) && !Util.contains(enchantment, UNBREAKING, VANISHING_CURSE)
                    && (enchantment == impact || !name.contains("soulbound")) && !name.contains("holding")
                    && !name.contains("mending");
        });
        this.skills = new SkillStorage(Skills.healing, Skills.penetration, Skills.vulnerability, Skills.penetration, Skills.endermanacle);
    }

    public static StaffStorage get(Entity entity) {
        return Capabilities.weapon.get(entity).storage(StorageType.staff);
    }

    @Override
    public Text getName() {
        return Translations.soulboundStaff;
    }

    @Override
    public StorageType<StaffStorage> type() {
        return StorageType.staff;
    }

    public int getFireballCooldown() {
        return this.fireballCooldown;
    }

    public void setFireballCooldown(int ticks) {
        this.fireballCooldown = ticks;
    }

    public void resetFireballCooldown() {
        this.fireballCooldown = (int) Math.round(20 / this.attribute(attackSpeed));
    }

    public int spell() {
        return this.spell;
    }

    public void setSpell(int spell) {
        this.spell = spell;
    }

    public void cycleSpells(int spells) {
        this.spell = Math.abs((this.spell + spells) % 2);

        this.sync();
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(AttributeModifierIdentifiers.attackSpeedModifier, "Weapon modifier", this.attributeRelative(attackSpeed), EntityAttributeModifier.Operation.ADDITION));
            modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(AttributeModifierIdentifiers.attackDamageModifier, "Weapon modifier", this.attributeRelative(attackDamage), EntityAttributeModifier.Operation.ADDITION));
        }

        return modifiers;
    }

    @Override
    public List<StatisticEntry> screenAttributes() {
         List<StatisticEntry> entries = new ReferenceArrayList<>();

        entries.add(new StatisticEntry(this.statistic(attackSpeed), new Translation("%s%s: %s", Translations.attackSpeedFormat, Translations.attackSpeedName, this.formatStatistic(attackSpeed))));
        entries.add(new StatisticEntry(this.statistic(attackDamage), new Translation("%s%s: %s", Translations.attackDamageFormat, Translations.attackDamageName, this.formatStatistic(attackDamage))));
        entries.add(new StatisticEntry(this.statistic(criticalStrikeRate), new Translation("%s%s: %s%%", Translations.criticalStrikeRateFormat, Translations.criticalStrikeRateName, this.formatStatistic(criticalStrikeRate))));

        return entries;
    }

    @Override
    public List<Text> tooltip() {
         NumberFormat format = DecimalFormat.getInstance();
         List<Text> tooltip = new ArrayList<>();

        tooltip.add(new Translation(" %s%s %s", Translations.attackSpeedFormat, format.format(this.attribute(attackSpeed)), Translations.attackSpeedName));
        tooltip.add(new Translation(" %s%s %s", Translations.attackDamageFormat, format.format(this.attributeTotal(attackDamage)), Translations.attackDamageName));
        tooltip.add(new LiteralText(""));
        tooltip.add(new LiteralText(""));

        if (this.attribute(criticalStrikeRate) > 0) {
            tooltip.add(new LiteralText(String.format(" %s%s%% %s", Translations.criticalStrikeRateFormat, format.format(this.attribute(criticalStrikeRate) * 100), Translations.criticalStrikeRateName)));
        }

        return tooltip;
    }

    @Override
    public Item getConsumableItem() {
        return SoulboundItems.staff;
    }

    @Override
    public double increase(StatisticType statistic, int points) {
        if (statistic == attackSpeed) {
            return 0.08;
        }

        if (statistic == attackDamage) {
            return 0.2;
        }

        if (statistic == criticalStrikeRate) {
            return 0.04;
        }

        return 0;
    }

    @Override
    public void tick() {
        if (this.fireballCooldown > 0) {
            this.fireballCooldown--;
        }
    }

    @Override
    public void serializeNBT(NbtCompound tag) {
        super.serializeNBT(tag);
        
        tag.putInt("spell", this.spell);
    }

    @Override
    public void deserializeNBT(NbtCompound tag) {
        super.deserializeNBT(tag);

        this.setSpell(tag.getInt("spell"));
    }

    @Override
    public NbtCompound clientTag() {
        NbtCompound tag = super.clientTag();

        tag.putInt("spell", this.spell);

        return tag;
    }
}
