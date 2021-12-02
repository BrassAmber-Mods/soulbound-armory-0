package soulboundarmory.component.soulbound.item;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import soulboundarmory.SoulboundArmoryClient;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.EnchantmentStorage;
import soulboundarmory.component.statistics.SkillStorage;
import soulboundarmory.component.statistics.Statistic;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.component.statistics.Statistics;
import soulboundarmory.client.gui.screen.AttributeTab;
import soulboundarmory.client.gui.screen.EnchantmentTab;
import soulboundarmory.client.gui.screen.SelectionTab;
import soulboundarmory.client.gui.screen.SkillTab;
import soulboundarmory.client.gui.screen.SoulboundScreen;
import soulboundarmory.client.gui.screen.SoulboundTab;
import soulboundarmory.client.gui.screen.StatisticEntry;
import soulboundarmory.config.Configuration;
import soulboundarmory.item.SoulboundItem;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.serial.CompoundSerializable;
import soulboundarmory.skill.Skill;
import soulboundarmory.skill.SkillContainer;
import soulboundarmory.util.ItemUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class ItemStorage<T extends ItemStorage<T>> implements CompoundSerializable {
    protected static final NumberFormat statisticFormat = DecimalFormat.getInstance();

    public EnchantmentStorage enchantments;

    protected final SoulboundComponent component;
    protected final PlayerEntity player;
    protected final Item item;
    protected final boolean client;

    protected SkillStorage skills;
    protected Statistics statistics;
    protected ItemStack itemStack;
    protected boolean unlocked;
    protected int boundSlot;
    protected int currentTab;

    public ItemStorage(SoulboundComponent component, Item item) {
        this.component = component;
        this.player = component.entity;
        this.client = component.entity.level.isClientSide;
        this.item = item;
        this.itemStack = this.newItemStack();
    }

    public abstract ITextComponent getName();

    public abstract List<StatisticEntry> screenAttributes();

    public abstract List<ITextComponent> tooltip();

    public abstract Item getConsumableItem();

    public abstract double increase(StatisticType statistic, int points);

    public abstract int getLevelXP(int level);

    public abstract StorageType<T> type();

    public abstract Class<? extends SoulboundItem> itemClass();

    public abstract Multimap<Attribute, AttributeModifier> attributeModifiers(Multimap<Attribute, AttributeModifier> modifiers, EquipmentSlotType slot);

    public static Optional<ItemStorage<?>> get(Entity entity, Item item) {
        return Components.soulbound(entity).flatMap(component -> component.storages().values().stream()).filter(storage -> storage.player == entity && storage.item() == item).findAny();
    }

    public static ItemStorage<?> get(Entity entity, StorageType<?> type) {
        return Components.soulbound(entity).flatMap(component -> component.storages().values().stream()).filter(storage -> storage.type() == type).findAny().orElse(null);
    }

    public PlayerEntity player() {
        return this.player;
    }

    public SoulboundComponent component() {
        return this.component;
    }

    public Item item() {
        return this.item;
    }

    public ItemStack menuEquippedStack() {
        for (var itemStack : this.player.getHandSlots()) {
            var item = itemStack.getItem();

            if (item == this.item() || item == this.getConsumableItem()) {
                return itemStack;
            }
        }

        return null;
    }

    public boolean isUnlocked() {
        return this.unlocked;
    }

    public void unlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public int size(Category category) {
        return this.statistics.size(category);
    }

    public Statistic statistic(StatisticType statistic) {
        return this.statistics.get(statistic);
    }

    public Statistic statistic(Category category, StatisticType statistic) {
        return this.statistics.get(category, statistic);
    }

    public int datum(StatisticType statistic) {
        return this.statistic(statistic).intValue();
    }

    public double attributeTotal(StatisticType statistic) {
        if (statistic == StatisticType.attackDamage) {
            var attackDamage = this.attribute(StatisticType.attackDamage);

            for (var entry : this.enchantments.entrySet()) {
                var enchantment = entry.getKey();

                if (entry.getValue() > 0) {
                    attackDamage += enchantment.getDamageBonus(this.enchantment(enchantment), CreatureAttribute.UNDEFINED);
                }
            }

            return attackDamage;
        }

        return this.attribute(statistic);
    }

    public double attributeRelative(StatisticType attribute) {
        if (attribute == StatisticType.attackSpeed) {
            return this.attribute(StatisticType.attackSpeed) - 4;
        }

        if (attribute == StatisticType.attackDamage) {
            return this.attribute(StatisticType.attackDamage) - 1;
        }

        if (attribute == StatisticType.reach) {
            return this.attribute(StatisticType.reach) - 3;
        }

        return this.attribute(attribute);
    }

    public double attribute(StatisticType type) {
        return this.statistics.get(type).doubleValue();
    }

    public boolean incrementStatistic(StatisticType type, double amount) {
        var leveledUp = false;

        if (type == StatisticType.experience) {
            var statistic = this.statistic(StatisticType.experience);
            statistic.add(amount);
            var xp = statistic.intValue();

            if (xp >= this.nextLevelXP() && this.canLevelUp()) {
                var nextLevelXP = this.nextLevelXP();

                this.incrementStatistic(StatisticType.level, 1);
                this.incrementStatistic(StatisticType.experience, -nextLevelXP);

                leveledUp = true;
            }

            if (xp < 0) {
                var currentLevelXP = this.getLevelXP(this.datum(StatisticType.level) - 1);

                this.incrementStatistic(StatisticType.level, -1);
                this.incrementStatistic(StatisticType.experience, currentLevelXP);
            }
        } else if (type == StatisticType.level) {
            var sign = (int) Math.signum(amount);

            for (var i = 0; i < Math.abs(amount); i++) {
                this.onLevelup(sign);
            }
        } else {
            this.statistics.add(type, amount);
        }

        this.updateItemStack();
        this.sync();

        return leveledUp;
    }

    public void incrementPoints(StatisticType type, int points) {
        var sign = (int) Math.signum(points);
        var statistic = this.statistic(type);

        for (var i = 0; i < Math.abs(points); i++) {
            if (sign > 0 && this.datum(StatisticType.attributePoints) > 0 || sign < 0 && statistic.isAboveMin()) {
                this.statistic(StatisticType.attributePoints).add(-sign);
                this.statistic(StatisticType.spentAttributePoints).add(sign);

                var change = sign * this.increase(type);

                statistic.add(change);
                statistic.incrementPoints();
            }
        }

        this.updateItemStack();
        this.sync();
    }

    public double increase(StatisticType statisticType) {
        var statistic = this.statistic(statisticType);

        return statistic == null ? 0 : this.increase(statisticType, statistic.getPoints());
    }

    public void set(StatisticType statistic, Number value) {
        this.statistics.put(statistic, value);

        this.sync();
    }

    public boolean canLevelUp() {
        var configuration = Configuration.instance();

        return this.datum(StatisticType.level) < configuration.maxLevel || configuration.maxLevel < 0;
    }

    public void onLevelup(int sign) {
        var statistic = this.statistic(StatisticType.level);
        statistic.add(sign);
        var level = statistic.intValue();
        var configuration = Configuration.instance();

        if (level % configuration.levelsPerEnchantment == 0) {
            this.incrementStatistic(StatisticType.enchantmentPoints, sign);
        }

        if (level % configuration.levelsPerSkillPoint == 0) {
            this.incrementStatistic(StatisticType.skillPoints, sign);
        }

        this.incrementStatistic(StatisticType.attributePoints, sign);
    }

    public Collection<SkillContainer> skills() {
        return this.skills.values();
    }

    public SkillContainer skill(ResourceLocation identifier) {
        return this.skill(Skill.registry.getValue(identifier));
    }

    public SkillContainer skill(Skill skill) {
        return this.skills.get(skill);
    }

    public boolean hasSkill(ResourceLocation identifier) {
        return this.hasSkill(Skill.registry.getValue(identifier));
    }

    public boolean hasSkill(Skill skill) {
        return this.skills.contains(skill);
    }

    public boolean hasSkill(Skill skill, int level) {
        return this.skills.contains(skill, level);
    }

    public void upgrade(SkillContainer skill) {
        //        if (this.isClientSide) {
        //            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_SKILL, new ExtendedPacketBuffer(this, item).writeString(skill.toString()));
        //        } else {
        var points = this.datum(StatisticType.skillPoints);
        var cost = skill.cost();

        if (skill.canLearn(points)) {
            skill.learn();

            this.incrementStatistic(StatisticType.skillPoints, -cost);
        } else if (skill.canUpgrade(points)) {
            skill.upgrade();

            this.incrementStatistic(StatisticType.skillPoints, -cost);
        }
        //        }
    }

    public int nextLevelXP() {
        return this.getLevelXP(this.datum(StatisticType.level));
    }

    public int enchantment(Enchantment enchantment) {
        return this.enchantments.get(enchantment);
    }

    public void addEnchantment(Enchantment enchantment, int value) {
        var current = this.enchantment(enchantment);
        var change = Math.max(0, current + value) - current;

        this.statistics.add(StatisticType.enchantmentPoints, -change);
        this.statistics.add(StatisticType.spentEnchantmentPoints, change);

        this.enchantments.add(enchantment, change);
        this.updateItemStack();

        this.sync();
    }

    public void reset() {
        for (var category : Category.registry) {
            this.reset(category);
        }

        this.unlocked = false;
        this.boundSlot = -1;
        this.currentTab = 0;

        this.sync();
    }

    public void reset(Category category) {
        if (category == Category.datum) {
            this.statistics.reset(Category.datum);
        } else if (category == Category.attribute) {
            for (var type : this.statistics.get(Category.attribute).values()) {
                this.incrementStatistic(StatisticType.attributePoints, type.getPoints());
                type.reset();
            }

            this.set(StatisticType.spentAttributePoints, 0);
            this.updateItemStack();
        } else if (category == Category.enchantment) {
            for (var enchantment : this.enchantments) {
                this.incrementStatistic(StatisticType.enchantmentPoints, this.enchantments.get(enchantment));
                this.enchantments.put(enchantment, 0);
            }

            this.statistic(StatisticType.spentEnchantmentPoints).setToMin();
            this.updateItemStack();
        } else if (category == Category.skill) {
            this.skills.reset();

            this.statistic(StatisticType.skillPoints).setToMin();
        }

        this.sync();
    }

    public boolean canUnlock() {
        return !this.unlocked && ItemUtil.isEquipped(this.player, this.getConsumableItem());
    }

    public boolean canConsume(Item item) {
        return this.getConsumableItem() == item;
    }

    public int boundSlot() {
        return this.boundSlot;
    }

    public void bindSlot(int boundSlot) {
        this.boundSlot = boundSlot;
    }

    public void unbindSlot() {
        this.boundSlot = -1;
    }

    public int tab() {
        return this.currentTab;
    }

    public void tab(int tab) {
        this.currentTab = tab;

        if (this.client) {
            this.sync();
        }
    }

    @SuppressWarnings("VariableUseSideOnly")
    public void refresh() {
        if (this.client) {
            if (SoulboundArmoryClient.client.screen instanceof SoulboundTab) {
                var handItems = ItemUtil.handItems(this.player);

                if (handItems.contains(this.item())) {
                    this.openGUI();
                } else if (handItems.contains(this.getConsumableItem())) {
                    this.openGUI(0);
                }
            }
        } else {
            Packets.clientRefresh.send(this.player, new ExtendedPacketBuffer(this));
        }
    }

    public void openGUI() {
        this.openGUI(ItemUtil.equippedStack(this.player.inventory, this.itemClass()) == null ? 0 : this.currentTab);
    }

    @SuppressWarnings({"LocalVariableDeclarationSideOnly", "VariableUseSideOnly", "MethodCallSideOnly"})
    public void openGUI(int tab) {
        if (this.client) {
            var currentScreen = SoulboundArmoryClient.client.screen;

            if (currentScreen instanceof SoulboundScreen screen && this.currentTab == tab) {
                screen.refresh();
            } else {
                SoulboundArmoryClient.client.setScreen(new SoulboundScreen(this.component, tab, new SelectionTab(), new AttributeTab(), new EnchantmentTab(), new SkillTab()));
            }
        } else {
            Packets.clientOpenGUI.send(this.player, new ExtendedPacketBuffer(this).writeInt(tab));
        }
    }

    public boolean itemEquipped() {
        return ItemUtil.isEquipped(this.player, this.item());
    }

    public boolean anyItemEquipped() {
        return this.menuEquippedStack() != null;
    }

    public void removeOtherItems() {
        for (var storage : this.component.storages().values()) {
            if (storage != this) {
                var player = this.player;

                for (var itemStack : ItemUtil.inventory(player)) {
                    if (itemStack.getItem() == storage.item()) {
                        player.inventory.removeItem(itemStack);
                    }
                }
            }
        }
    }

    public ItemStack stack() {
        return this.itemStack;
    }

    protected void updateItemStack() {
        var itemStack = this.itemStack = this.newItemStack();

        for (var slot : EquipmentSlotType.values()) {
            var attributeModifiers = this.attributeModifiers(slot);

            for (var attribute : attributeModifiers.keySet()) {
                for (var modifier : attributeModifiers.get(attribute)) {
                    itemStack.addAttributeModifier(attribute, modifier, EquipmentSlotType.MAINHAND);
                }
            }
        }

        for (var entry : this.enchantments.entrySet()) {
            int level = entry.getValue();

            if (level > 0) {
                itemStack.enchant(entry.getKey(), level);
            }
        }
    }

    protected ItemStack newItemStack() {
        var itemStack = new ItemStack(this.item);
        //        Capabilities.itemData.get(itemStack).storage = this;

        return itemStack;
    }

    public Multimap<Attribute, AttributeModifier> attributeModifiers(EquipmentSlotType slot) {
        return this.attributeModifiers(LinkedHashMultimap.create(), slot);
    }

    protected String formatStatistic(StatisticType statistic) {
        var value = this.attributeTotal(statistic);
        return statisticFormat.format(statistic == StatisticType.criticalStrikeRate ? value * 100 : value);
    }

    @Override
    public void serializeNBT(CompoundNBT tag) {
        tag.put("statistics", this.statistics.serializeNBT());
        tag.put("enchantments", this.enchantments.serializeNBT());
        tag.put("skills", this.skills.serializeNBT());
        tag.putBoolean("unlocked", this.unlocked);
        tag.putInt("slot", this.boundSlot());
        tag.putInt("tab", this.tab());
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        this.statistics.deserializeNBT(tag.getCompound("statistics"));
        this.enchantments.deserializeNBT(tag.getCompound("enchantments"));
        this.skills.deserializeNBT(tag.getCompound("skills"));
        this.unlocked(tag.getBoolean("unlocked"));
        this.bindSlot(tag.getInt("slot"));
        this.tab(tag.getInt("tab"));

        this.updateItemStack();
    }

    public CompoundNBT clientTag() {
        var tag = new CompoundNBT();
        tag.putInt("tab", this.currentTab);

        return tag;
    }

    public void sync() {
        if (this.client) {
            Packets.serverSync.send(new ExtendedPacketBuffer(this).writeNbt(this.clientTag()));
        } else {
            Packets.clientSync.send(this.player, new ExtendedPacketBuffer(this).writeNbt(this.serializeNBT()));
        }
    }

    public void tick() {}
}
