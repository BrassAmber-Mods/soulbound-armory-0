package soulboundarmory.component.soulbound.item.tool;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.TierSortingRegistry;
import soulboundarmory.client.gui.screen.AttributeTab;
import soulboundarmory.client.gui.screen.EnchantmentTab;
import soulboundarmory.client.gui.screen.SelectionTab;
import soulboundarmory.client.gui.screen.SkillTab;
import soulboundarmory.client.gui.screen.SoulboundTab;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Statistic;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.config.Configuration;
import soulboundarmory.entity.Attributes;
import soulboundarmory.registry.Skills;
import soulboundarmory.registry.SoulboundItems;
import soulboundarmory.util.Util;

public abstract class ToolComponent<T extends ItemComponent<T>> extends ItemComponent<T> {
    protected ToolMaterial material = ToolMaterials.WOOD;
    protected ToolMaterial nextMaterial;

    public ToolComponent(SoulboundComponent<?> component) {
        super(component);

        this.skills.add(Skills.circumspection, Skills.enderPull);
    }

    public Text materialName() {
        return Translations.toolMaterial(this.material);
    }

    public void absorb() {
        var stack = this.player.getOffHandStack();

        if (stack.getItem() instanceof ToolItem tool && this.canAbsorb(stack)) {
            var tiers = TierSortingRegistry.getSortedTiers();

            if (tiers.indexOf(this.material) >= tiers.indexOf(tool.getMaterial())) {
                this.player.sendMessage(Translations.cannotAbsorbWeaker, true);
            } else {
                if (stack.isDamaged()) {
                    this.player.sendMessage(Translations.cannotAbsorbDamaged, true);
                } else {
                    stack.setCount(0);
                    this.nextMaterial = tool.getMaterial();
                    this.statistic(StatisticType.upgradeProgress).max(1);
                    this.synchronize();
                }
            }
        }
    }

    @Override
    public int getLevelXP(int level) {
        return this.canLevelUp()
            ? Configuration.instance().initialToolXP + (int) Math.round(4 * Math.pow(level, 1.25))
            : -1;
    }

    @Override
    public void mined(BlockState state, BlockPos position) {
        if (!this.isClient() && this.itemStack.isSuitableFor(state)) {
            var delta = Math.max(1, state.calcBlockBreakingDelta(this.player, this.player.world, position));
            var xp = Math.round(state.getHardness(this.player.world, position)) + 4 * (1 - delta);
            this.incrementStatistic(StatisticType.experience, delta == 1 ? Math.min(10, xp) : xp);
        }
    }

    @Override
    public ToolMaterial material() {
        return SoulboundItems.material(this.material);
    }

    @Override
    public void incrementAttributePoints(StatisticType type, int points) {
        super.incrementAttributePoints(type, points);

        var progress = this.statistic(StatisticType.upgradeProgress);

        if (this.nextMaterial != null && progress.intValue() == 1) {
            progress.setToMin();
            progress.max();
            this.material = this.nextMaterial;
            this.synchronize();
        }
    }

    @Override
    public void reset() {
        super.reset();

        this.nextMaterial = null;
        this.material = ToolMaterials.WOOD;
        this.synchronize();
    }

    @Override
    public void attributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(ForgeMod.REACH_DISTANCE.get(), this.toolModifier(Attributes.reach, StatisticType.reach));
        }
    }

    @Override
    public List<SoulboundTab> tabs() {
        return List.of(new SelectionTab(Translations.guiToolSelection), new AttributeTab(), new EnchantmentTab(), new SkillTab());
    }

    @Override
    public Map<Statistic, Text> screenAttributes() {
        var attributes = Util.map(
            this.statisticEntry(StatisticType.efficiency, Translations.guiToolEfficiency),
            this.statisticEntry(StatisticType.reach, Translations.guiReach)
        );

        if (this.nextMaterial != null) {
            Util.add(attributes, this.statisticEntry(StatisticType.upgradeProgress, Translations.guiUpgradeProgress, Translations.toolMaterial(this.nextMaterial)));
        }

        return attributes;
    }

    @Override
    public void serialize(NbtCompound tag) {
        super.serialize(tag);

        if (this.material != null) {
            tag.putString("material", TierSortingRegistry.getName(this.material).toString());
        }

        if (this.nextMaterial != null) {
            tag.putString("nextMaterial", TierSortingRegistry.getName(this.nextMaterial).toString());
        }
    }

    @Override
    public void deserialize(NbtCompound tag) {
        super.deserialize(tag);

        this.material = Objects.requireNonNullElse(TierSortingRegistry.byName(new Identifier(tag.getString("material"))), this.material);
        this.nextMaterial = TierSortingRegistry.byName(new Identifier(tag.getString("nextMaterial")));
    }

    protected abstract Tag<Block> tag();

    protected abstract boolean canAbsorb(ItemStack stack);
}
