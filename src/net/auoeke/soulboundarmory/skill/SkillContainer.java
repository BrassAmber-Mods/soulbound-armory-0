package net.auoeke.soulboundarmory.skill;

import java.util.List;
import java.util.Set;
import net.auoeke.soulboundarmory.capability.statistics.SkillStorage;
import net.auoeke.soulboundarmory.serial.CompoundSerializable;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.auoeke.cell.client.gui.screen.CellScreen;

public class SkillContainer implements Comparable<SkillContainer>, CompoundSerializable {
    protected final Skill skill;
    protected final int maxLevel;
    protected final SkillStorage storage;

    protected boolean learned;
    protected int level;

    public SkillContainer(Skill skill, SkillStorage storage) {
        this(skill, storage, false, 0);
    }

    public SkillContainer(Skill skill, SkillStorage storage, boolean learned, int level) {
        this.skill = skill;
        this.maxLevel = skill.maxLevel;
        this.storage = storage;
        this.learned = learned;
        this.level = level;
    }

    public Skill skill() {
        return this.skill;
    }

    public Set<Skill> dependencies() {
        return this.skill.dependencies();
    }

    public boolean hasDependencies() {
        return this.skill.hasDependencies();
    }

    public int tier() {
        return this.skill.tier();
    }

    public int level() {
        return this.level;
    }

    public void level(int level) {
        this.level = level;
    }

    public void upgrade() {
        this.level++;
    }

    public boolean canUpgrade(int points) {
        return this.canUpgrade() && points >= this.cost();
    }

    public boolean canUpgrade() {
        return this.learned && (this.cost() >= 0 || this.maxLevel < 0 || this.level < this.maxLevel);
    }

    public int cost() {
        return this.skill.cost(this.learned, this.level);
    }

    public boolean learned() {
        return this.learned;
    }

    public boolean canLearn() {
        for (Skill dependency : this.skill.dependencies()) {
            if (!this.storage.get(dependency).learned()) {
                return false;
            }
        }

        return !this.learned;
    }

    public boolean canLearn(int points) {
        return this.canLearn() && points >= this.cost();
    }

    public void learn() {
        this.learned = true;
    }

    @Override
    public int compareTo(SkillContainer other) {
        int tierDifference = this.skill.tier() - other.skill.tier();

        return tierDifference == 0 ? this.level() - other.level() : tierDifference;
    }

    @OnlyIn(Dist.CLIENT)
    public Text name() {
        return this.skill.name();
    }

    @OnlyIn(Dist.CLIENT)
    public List<Text> tooltip() {
        return this.skill.tooltip();
    }

    @OnlyIn(Dist.CLIENT)
    public void render(CellScreen screen, MatrixStack matrices, int x, int y, int zOffset) {
        this.skill.render(screen, matrices, this.level, x, y, zOffset);
    }

    @Override
    public void serializeNBT(NbtCompound tag) {
        tag.putBoolean("learned", this.learned);
        tag.putInt("level", this.level);
    }

    @Override
    public void deserializeNBT(NbtCompound tag) {
        this.learned = tag.getBoolean("learned");
        this.level = tag.getInt("level");
    }
}
