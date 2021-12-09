package soulboundarmory.skill;

import cell.client.gui.screen.CellScreen;
import java.util.List;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import soulboundarmory.component.statistics.SkillStorage;
import soulboundarmory.serial.CompoundSerializable;

public class SkillContainer implements Comparable<SkillContainer>, CompoundSerializable {
    public final Skill skill;
    public final List<SkillContainer> dependencies;

    protected final SkillStorage storage;
    protected boolean learned;
    protected int level;

    public SkillContainer(Skill skill, SkillStorage storage) {
        this(skill, storage, false, 0);
    }

    public SkillContainer(Skill skill, SkillStorage storage, boolean learned, int level) {
        this.skill = skill;
        this.storage = storage;
        this.learned = learned;
        this.level = level;
        this.dependencies = skill.dependencies.stream().map(storage::get).toList();
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
        return this.learned && (this.cost() >= 0 || this.skill.maxLevel < 0 || this.level < this.skill.maxLevel);
    }

    public int cost() {
        return this.skill.cost(this.learned, this.level);
    }

    public boolean learned() {
        return this.learned;
    }

    public void learn() {
        this.learned = true;
    }

    public boolean canLearn() {
        return !this.learned && this.dependencies.stream().allMatch(SkillContainer::learned);
    }

    public boolean canLearn(int points) {
        return this.canLearn() && points >= this.cost();
    }

    @Override
    public int compareTo(SkillContainer other) {
        var tierDifference = this.skill.tier() - other.skill.tier();

        return tierDifference == 0 ? this.level() - other.level() : tierDifference;
    }

    public Text name() {
        return this.skill.name();
    }

    public List<? extends Text> tooltip() {
        return this.skill.tooltip();
    }

    public void render(CellScreen screen, MatrixStack matrices, int x, int y, int zOffset) {
        this.skill.render(screen, matrices, this.level, x, y, zOffset);
    }

    public void reset() {
        this.learned = false;
        this.level = 0;
    }

    @Override
    public void serialize(NbtCompound tag) {
        tag.putBoolean("learned", this.learned);
        tag.putInt("level", this.level);
    }

    @Override
    public void deserialize(NbtCompound tag) {
        this.learned = tag.getBoolean("learned");
        this.level = tag.getInt("level");
    }
}
