package soulboundarmory.component.soulbound.item.weapon;

import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.registry.Skills;
import soulboundarmory.registry.SoulboundItems;

public class StaffComponent extends WeaponComponent<StaffComponent> {
    protected int spell;
    protected int fireballCooldown;

    public StaffComponent(SoulboundComponent<?> component) {
        super(component);

        this.statistics
            .statistics(StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints)
            .constant(3, StatisticType.reach)
            .min(0.48, StatisticType.attackSpeed)
            .min(8, StatisticType.attackDamage);

        this.enchantments.initialize(enchantment -> {
            var name = enchantment.getTranslationKey().toLowerCase();
            return !name.contains("holding") && (enchantment == SoulboundArmory.impact || !name.contains("soulbound"));
        });

        this.skills.add(Skills.healing, Skills.vulnerability, Skills.penetration, Skills.endermanacle);
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
        this.fireballCooldown = (int) Math.round(20 / this.attackSpeed());
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
    public Item consumableItem() {
        return null;
    }

    @Override
    public double increase(StatisticType type) {
        if (type == StatisticType.attackSpeed) return 0.08;
        if (type == StatisticType.attackDamage) return 0.2;
        if (type == StatisticType.criticalHitRate) return 0.04;

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
