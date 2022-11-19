package soulboundarmory.skill;

import java.util.List;
import java.util.Set;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import soulboundarmory.component.statistics.SkillMap;
import soulboundarmory.module.gui.widget.Widget;
import soulboundarmory.serial.Serializable;

public final class SkillInstance implements Comparable<SkillInstance>, Serializable {
	public final Set<SkillInstance> dependencies = new ReferenceOpenHashSet<>();
	public final Skill skill;

	private int level;

	public SkillInstance(Skill skill) {
		this.skill = skill;
	}

	public void initializeDependencies(SkillMap storage) {
		this.dependencies.addAll(this.skill.dependencies().stream().map(storage::get).toList());
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

	public boolean dependenciesFulfilled() {
		return this.dependencies.stream().allMatch(SkillInstance::learned);
	}

	public boolean canUpgrade() {
		return this.dependenciesFulfilled() && (this.skill.maxLevel < 1 || this.level < this.skill.maxLevel);
	}

	public boolean canUpgrade(int points) {
		return this.canUpgrade() && points >= this.cost();
	}

	public void upgrade() {
		this.level++;
	}

	public void downgrade() {
		this.level--;
	}

	public int cost() {
		return this.skill.cost(this.level + 1);
	}

	public int spentPoints() {
		var points = 0;

		for (var level = this.level; level > 0; level--) {
			points += this.skill.cost(level);
		}

		return points;
	}

	public boolean learned() {
		return this.level > 0;
	}

	@Override
	public int compareTo(SkillInstance other) {
		var tierDifference = this.skill.tier() - other.skill.tier();
		return tierDifference == 0 ? this.level() - other.level() : tierDifference;
	}

	public Text name() {
		return this.skill.name();
	}

	public List<? extends StringVisitable> tooltip() {
		return this.skill.tooltip();
	}

	public void render(Widget<?> tab, MatrixStack matrixes) {
		this.skill.render(tab, this.level);
	}

	public void reset() {
		this.level = 0;
	}

	@Override
	public void serialize(NbtCompound tag) {
		tag.putInt("level", this.level);
	}

	@Override
	public void deserialize(NbtCompound tag) {
		this.level = tag.getInt("level");
	}
}
