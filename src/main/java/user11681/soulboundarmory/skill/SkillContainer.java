package user11681.soulboundarmory.skill;

import java.util.List;
import javax.annotation.Nonnull;
import nerdhub.cardinal.components.api.util.NbtSerializable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import user11681.soulboundarmory.component.statistics.SkillStorage;
import user11681.spun.client.gui.screen.SpunScreen;

public class SkillContainer implements Comparable<SkillContainer>, NbtSerializable {
    protected final Skill skill;
    protected final int maxLevel;
    protected final SkillStorage storage;

    protected boolean learned;
    protected int level;

    public SkillContainer(final Skill skill, final SkillStorage storage) {
        this(skill, storage, false, 0);
    }

    public SkillContainer(final Skill skill, final SkillStorage storage, final boolean learned, final int level) {
        this.skill = skill;
        this.maxLevel = skill.maxLevel;
        this.storage = storage;
        this.learned = learned;
        this.level = level;
    }

    public Skill getSkill() {
        return this.skill;
    }

    public List<Skill> getDependencies() {
        return this.skill.getDependencies();
    }

    public boolean hasDependencies() {
        return this.skill.hasDependencies();
    }

    public int getTier() {
        return this.skill.getTier();
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public void upgrade() {
        this.level++;
    }

    public boolean canBeUpgraded(final int points) {
        return this.canBeUpgraded() && points >= this.getCost();
    }

    public boolean canBeUpgraded() {
        return this.learned && (this.getCost() >= 0 || this.maxLevel < 0 || this.level < this.maxLevel);
    }

    public int getCost() {
        return this.skill.getCost(this.learned, this.level);
    }

    public boolean isLearned() {
        return this.learned;
    }

    public boolean canBeLearned() {
        for (final Skill dependency : this.skill.getDependencies()) {
            if (!this.storage.get(dependency).isLearned()) {
                return false;
            }
        }

        return !this.learned;
    }

    public boolean canBeLearned(final int points) {
        return this.canBeLearned() && points >= this.getCost();
    }

    public void learn() {
        this.learned = true;
    }

    @Override
    public int compareTo(final SkillContainer other) {
        final int tierDifference = this.skill.getTier() - other.skill.getTier();

        return tierDifference != 0 ? tierDifference : this.getLevel() - other.getLevel();
    }

    @Environment(EnvType.CLIENT)
    public Text getName() {
        return this.skill.getName();
    }

    @Environment(EnvType.CLIENT)
        public List<String> getTooltip() {
        return this.skill.getTooltip();
    }

    @Environment(EnvType.CLIENT)
    public void render(final SpunScreen screen, final MatrixStack matrices, final int x, final int y, final int zOffset) {
        this.skill.render(screen, matrices, this.level, x, y, zOffset);
    }

        @Override
    public NbtCompound toTag(final NbtCompound tag) {
        tag.putBoolean("learned", this.learned);
        tag.putInt("level", this.level);

        return tag;
    }

    @Override
    public void fromTag(final NbtCompound tag) {
        this.learned = tag.getBoolean("learned");
        this.level = tag.getInt("level");
    }
}
