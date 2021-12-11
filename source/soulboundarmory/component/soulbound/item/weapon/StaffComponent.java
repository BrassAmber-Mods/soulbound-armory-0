package soulboundarmory.component.soulbound.item.weapon;

import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.client.gui.screen.StatisticEntry;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.registry.Skills;
import soulboundarmory.registry.SoulboundItems;
import soulboundarmory.util.AttributeModifierIdentifiers;

import static net.minecraft.enchantment.Enchantments.UNBREAKING;

public class StaffComponent extends WeaponComponent<StaffComponent> {
    protected int spell;
    protected int fireballCooldown;

    public StaffComponent(SoulboundComponent<?> component) {
        super(component);

        this.statistics
            .category(Category.datum, StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints)
            .category(Category.attribute, StatisticType.attackSpeed, StatisticType.attackDamage, StatisticType.criticalStrikeRate)
            .min(0.48, StatisticType.attackSpeed).min(8, StatisticType.attackDamage)
            .max(1, StatisticType.criticalStrikeRate)
            .max(4, StatisticType.attackSpeed);

        this.enchantments.add(enchantment -> {
            var name = enchantment.getTranslationKey().toLowerCase();

            return enchantment.type.isAcceptableItem(this.item())
                && !enchantment.isCursed()
                && enchantment != UNBREAKING
                && (enchantment == SoulboundArmory.impact || !name.contains("soulbound"))
                && !name.contains("holding")
                && !name.contains("mending");
        });

        this.skills.add(Skills.healing, Skills.penetration, Skills.vulnerability, Skills.penetration, Skills.endermanacle);
    }

    @Override
    public ItemComponentType<StaffComponent> type() {
        return ItemComponentType.staff;
    }

    @Override
    public Item item() {
        return SoulboundItems.staff;
    }

    @Override
    public Text name() {
        return Translations.guiStaff;
    }

    public int fireballCooldown() {
        return this.fireballCooldown;
    }

    public void resetFireballCooldown() {
        this.fireballCooldown = (int) Math.round(20 / this.doubleValue(StatisticType.attackSpeed));
    }

    public int spell() {
        return this.spell;
    }

    public void spell(int spell) {
        this.spell = spell;
    }

    public void cycleSpells(int spells) {
        this.spell = Math.abs((this.spell + spells) % 2);

        if (this.isClient()) {
            Packets.serverSpell.send(new ExtendedPacketBuffer().writeByte(this.spell));
        }
    }

    @Override
    public void attributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, this.weaponModifier(AttributeModifierIdentifiers.ItemAccess.attackSpeedModifier, StatisticType.attackSpeed));
            modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, this.weaponModifier(AttributeModifierIdentifiers.ItemAccess.attackDamageModifier, StatisticType.attackDamage));
        }
    }

    @Override
    public List<StatisticEntry> screenAttributes() {
        return new ReferenceArrayList<>(List.of(
            new StatisticEntry(this.statistic(StatisticType.attackSpeed), Translations.guiAttackSpeed.format(this.formatStatistic(StatisticType.attackSpeed))),
            new StatisticEntry(this.statistic(StatisticType.attackDamage), Translations.guiAttackDamage.format(this.formatStatistic(StatisticType.attackDamage))),
            new StatisticEntry(this.statistic(StatisticType.criticalStrikeRate), Translations.guiCriticalStrikeRate.format(this.formatStatistic(StatisticType.criticalStrikeRate)))
        ));
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

        if (this.doubleValue(StatisticType.criticalStrikeRate) > 0) {
            tooltip.add(Translations.tooltipCriticalStrikeRate.translate(format.format(this.formatStatistic(StatisticType.criticalStrikeRate))));
        }

        return tooltip;
    }

    @Override
    public Item consumableItem() {
        return null;
    }

    @Override
    public double increase(StatisticType statistic) {
        if (statistic == StatisticType.attackSpeed) return 0.08;
        if (statistic == StatisticType.attackDamage) return 0.2;
        if (statistic == StatisticType.criticalStrikeRate) return 0.04;

        return 0;
    }

    @Override
    public void tick() {
        if (this.fireballCooldown > 0) {
            this.fireballCooldown--;
        }
    }

    @Override
    public void serialize(NbtCompound tag) {
        super.serialize(tag);

        tag.putByte("spell", (byte) this.spell);
    }

    @Override
    public void deserialize(NbtCompound tag) {
        super.deserialize(tag);

        this.spell(tag.getByte("spell"));
    }
}
