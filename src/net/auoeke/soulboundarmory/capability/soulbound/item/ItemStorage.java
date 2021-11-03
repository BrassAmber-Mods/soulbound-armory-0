package net.auoeke.soulboundarmory.capability.soulbound.item;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import net.auoeke.soulboundarmory.SoulboundArmoryClient;
import net.auoeke.soulboundarmory.capability.Capabilities;
import net.auoeke.soulboundarmory.capability.CapabilityContainer;
import net.auoeke.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import net.auoeke.soulboundarmory.capability.statistics.Category;
import net.auoeke.soulboundarmory.capability.statistics.SkillStorage;
import net.auoeke.soulboundarmory.capability.statistics.Statistic;
import net.auoeke.soulboundarmory.capability.statistics.StatisticType;
import net.auoeke.soulboundarmory.capability.statistics.Statistics;
import net.auoeke.soulboundarmory.client.gui.screen.AttributeTab;
import net.auoeke.soulboundarmory.client.gui.screen.EnchantmentTab;
import net.auoeke.soulboundarmory.client.gui.screen.SelectionTab;
import net.auoeke.soulboundarmory.client.gui.screen.SkillTab;
import net.auoeke.soulboundarmory.client.gui.screen.SoulboundScreen;
import net.auoeke.soulboundarmory.client.gui.screen.SoulboundTab;
import net.auoeke.soulboundarmory.client.gui.screen.StatisticEntry;
import net.auoeke.soulboundarmory.config.Configuration;
import net.auoeke.soulboundarmory.item.SoulboundItem;
import net.auoeke.soulboundarmory.network.ExtendedPacketBuffer;
import net.auoeke.soulboundarmory.registry.Packets;
import net.auoeke.soulboundarmory.serial.CompoundSerializable;
import net.auoeke.soulboundarmory.skill.Skill;
import net.auoeke.soulboundarmory.skill.SkillContainer;
import net.auoeke.soulboundarmory.util.ItemUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.auoeke.soulboundarmory.capability.statistics.EnchantmentStorage;

public abstract class ItemStorage<T extends ItemStorage<T>> implements CompoundSerializable {
    protected static final NumberFormat statisticFormat = DecimalFormat.getInstance();

    public EnchantmentStorage enchantments;

    protected final SoulboundCapability capability;
    protected final PlayerEntity player;
    protected final Item item;
    protected final boolean isClient;

    protected SkillStorage skills;
    protected Statistics statistics;
    protected ItemStack itemStack;
    protected boolean unlocked;
    protected int boundSlot;
    protected int currentTab;

    public ItemStorage(SoulboundCapability capability, Item item) {
        this.capability = capability;
        this.player = capability.entity;
        this.isClient = capability.entity.world.isClient;
        this.item = item;
        this.itemStack = this.newItemStack();
    }

    public abstract Text getName();

    public abstract List<StatisticEntry> screenAttributes();

    public abstract List<Text> tooltip();

    public abstract Item getConsumableItem();

    public abstract double increase(StatisticType statistic, int points);

    public abstract int getLevelXP(int level);

    public abstract StorageType<T> type();

    public abstract Class<? extends SoulboundItem> itemClass();

    public abstract Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot);

    public static ItemStorage<?> get(Entity entity, Item item) {
        return Capabilities.get(entity).flatMap(component -> component.storages().values().stream()).filter(storage -> storage.player == entity && storage.getItem() == item).findAny().orElse(null);
    }

    public static ItemStorage<?> get(Entity entity, StorageType<?> type) {
        for (CapabilityContainer<? extends SoulboundCapability> component : Capabilities.soulboundCapabilities) {
            for (ItemStorage<?> storage : component.get(entity).storages().values()) {
                if (storage.type() == type) {
                    return storage;
                }
            }
        }

        return null;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }

    public SoulboundCapability getCapability() {
        return this.capability;
    }

    public Item getItem() {
        return this.item;
    }

    public ItemStack getMenuEquippedStack() {
        for (ItemStack itemStack : this.player.getItemsHand()) {
            Item item = itemStack.getItem();

            if (item == this.getItem() || item == this.getConsumableItem()) {
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
            double attackDamage = this.attribute(StatisticType.attackDamage);

            for (Entry<Enchantment, Integer> entry : this.enchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();

                if (entry.getValue() > 0) {
                    attackDamage += enchantment.getAttackDamage(this.enchantment(enchantment), EntityGroup.DEFAULT);
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
        boolean leveledUp = false;

        if (type == StatisticType.experience) {
            Statistic statistic = this.statistic(StatisticType.experience);
            statistic.add(amount);
            int xp = statistic.intValue();

            if (xp >= this.nextLevelXP() && this.canLevelUp()) {
                int nextLevelXP = this.nextLevelXP();

                this.incrementStatistic(StatisticType.level, 1);
                this.incrementStatistic(StatisticType.experience, -nextLevelXP);

                leveledUp = true;
            }

            if (xp < 0) {
                int currentLevelXP = this.getLevelXP(this.datum(StatisticType.level) - 1);

                this.incrementStatistic(StatisticType.level, -1);
                this.incrementStatistic(StatisticType.experience, currentLevelXP);
            }
        } else if (type == StatisticType.level) {
            int sign = (int) Math.signum(amount);

            for (int i = 0; i < Math.abs(amount); i++) {
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
        int sign = (int) Math.signum(points);
        Statistic statistic = this.statistic(type);

        for (int i = 0; i < Math.abs(points); i++) {
            if (sign > 0 && this.datum(StatisticType.attributePoints) > 0 || sign < 0 && statistic.isAboveMin()) {
                this.statistic(StatisticType.attributePoints).add(-sign);
                this.statistic(StatisticType.spentAttributePoints).add(sign);

                double change = sign * this.increase(type);

                statistic.add(change);
                statistic.incrementPoints();
            }
        }

        this.updateItemStack();
        this.sync();
    }

    public double increase(StatisticType statisticType) {
        Statistic statistic = this.statistic(statisticType);

        return statistic == null ? 0 : this.increase(statisticType, statistic.getPoints());
    }

    public void set(StatisticType statistic, Number value) {
        this.statistics.put(statistic, value);

        this.sync();
    }

    public boolean canLevelUp() {
        Configuration configuration = Configuration.instance();

        return this.datum(StatisticType.level) < configuration.maxLevel || configuration.maxLevel < 0;
    }

    public void onLevelup(int sign) {
        Statistic statistic = this.statistic(StatisticType.level);
        statistic.add(sign);
        int level = statistic.intValue();
        Configuration configuration = Configuration.instance();

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

    public SkillContainer skill(Identifier identifier) {
        return this.skill(Skill.registry.getValue(identifier));
    }

    public SkillContainer skill(Skill skill) {
        return this.skills.get(skill);
    }

    public boolean hasSkill(Identifier identifier) {
        return this.hasSkill(Skill.registry.getValue(identifier));
    }

    public boolean hasSkill(Skill skill) {
        return this.skills.contains(skill);
    }

    public boolean hasSkill(Skill skill, int level) {
        return this.skills.contains(skill, level);
    }

    public void upgrade(SkillContainer skill) {
        //        if (this.isClient) {
        //            MainClient.PACKET_REGISTRY.sendToServer(Packets.C2S_SKILL, new ExtendedPacketBuffer(this, item).writeString(skill.toString()));
        //        } else {
        int points = this.datum(StatisticType.skillPoints);
        int cost = skill.cost();

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
        int current = this.enchantment(enchantment);
        int change = Math.max(0, current + value) - current;

        this.statistics.add(StatisticType.enchantmentPoints, -change);
        this.statistics.add(StatisticType.spentEnchantmentPoints, change);

        this.enchantments.add(enchantment, change);
        this.updateItemStack();

        this.sync();
    }

    public void reset() {
        for (Category category : Category.registry) {
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
            for (Statistic type : this.statistics.get(Category.attribute).values()) {
                this.incrementStatistic(StatisticType.attributePoints, type.getPoints());
                type.reset();
            }

            this.set(StatisticType.spentAttributePoints, 0);
            this.updateItemStack();
        } else if (category == Category.enchantment) {
            for (Enchantment enchantment : this.enchantments) {
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

        if (this.isClient) {
            this.sync();
        }
    }

    @SuppressWarnings("VariableUseSideOnly")
    public void refresh() {
        if (this.isClient) {
            if (SoulboundArmoryClient.client.currentScreen instanceof SoulboundTab) {
                List<Item> handItems = ItemUtil.handItems(this.player);

                if (handItems.contains(this.getItem())) {
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
        if (this.isClient) {
            Screen currentScreen = SoulboundArmoryClient.client.currentScreen;

            if (currentScreen instanceof SoulboundScreen screen && this.currentTab == tab) {
                screen.refresh();
            } else {
                SoulboundArmoryClient.client.openScreen(new SoulboundScreen(this.capability, tab, new SelectionTab(), new AttributeTab(), new EnchantmentTab(), new SkillTab()));
            }
        } else {
            Packets.clientOpenGUI.send(this.player, new ExtendedPacketBuffer(this).writeInt(tab));
        }
    }

    public boolean itemEquipped() {
        return ItemUtil.isEquipped(this.player, this.getItem());
    }

    public boolean anyItemEquipped() {
        return this.getMenuEquippedStack() != null;
    }

    public void removeOtherItems() {
        for (ItemStorage<?> storage : this.capability.storages().values()) {
            if (storage != this) {
                PlayerEntity player = this.player;

                for (ItemStack itemStack : ItemUtil.inventory(player)) {
                    if (itemStack.getItem() == storage.getItem()) {
                        player.inventory.removeOne(itemStack);
                    }
                }
            }
        }
    }

    public ItemStack itemStack() {
        return this.itemStack;
    }

    protected void updateItemStack() {
        ItemStack itemStack = this.itemStack = this.newItemStack();

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers = this.attributeModifiers(slot);

            for (EntityAttribute attribute : attributeModifiers.keySet()) {
                for (EntityAttributeModifier modifier : attributeModifiers.get(attribute)) {
                    itemStack.addAttributeModifier(attribute, modifier, EquipmentSlot.MAINHAND);
                }
            }
        }

        for (Entry<Enchantment, Integer> entry : this.enchantments.entrySet()) {
            int level = entry.getValue();

            if (level > 0) {
                itemStack.addEnchantment(entry.getKey(), level);
            }
        }
    }

    protected ItemStack newItemStack() {
        ItemStack itemStack = new ItemStack(this.item);
        //        Capabilities.itemData.get(itemStack).storage = this;

        return itemStack;
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers(EquipmentSlot slot) {
        return this.attributeModifiers(LinkedHashMultimap.create(), slot);
    }

    protected String formatStatistic(StatisticType statistic) {
        double value = this.attributeTotal(statistic);

        return statisticFormat.format(statistic == StatisticType.criticalStrikeRate ? value * 100 : value);
    }

    @Override
    public void deserializeNBT(NbtCompound tag) {
        this.statistics.deserializeNBT(tag.getCompound("statistics"));
        this.enchantments.deserializeNBT(tag.getCompound("enchantments"));
        this.skills.deserializeNBT(tag.getCompound("skills"));
        this.unlocked(tag.getBoolean("unlocked"));
        this.bindSlot(tag.getInt("slot"));
        this.tab(tag.getInt("tab"));

        this.updateItemStack();
    }

    @Override
    public void serializeNBT(NbtCompound tag) {
        tag.put("statistics", this.statistics.serializeNBT());
        tag.put("enchantments", this.enchantments.serializeNBT());
        tag.put("skills", this.skills.serializeNBT());
        tag.putBoolean("unlocked", this.unlocked);
        tag.putInt("slot", this.boundSlot());
        tag.putInt("tab", this.tab());
    }

    public NbtCompound clientTag() {
        NbtCompound tag = new NbtCompound();
        tag.putInt("tab", this.currentTab);

        return tag;
    }

    public void sync() {
        if (this.isClient) {
            Packets.serverSync.send(new ExtendedPacketBuffer(this).writeNbt(this.clientTag()));
        } else {
            Packets.clientSync.send(this.player, new ExtendedPacketBuffer(this).writeNbt(this.tag()));
        }
    }

    public void tick() {}
}
