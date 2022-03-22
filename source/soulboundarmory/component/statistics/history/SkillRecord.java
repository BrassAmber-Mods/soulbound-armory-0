package soulboundarmory.component.statistics.history;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.config.Configuration;
import soulboundarmory.skill.Skill;
import soulboundarmory.skill.SkillInstance;
import soulboundarmory.skill.Skills;

public final class SkillRecord extends Record {
    private Skill skill;
    private int points;

    public SkillRecord(ItemComponent<?> component, Skill skill, int points) {
        super(component);

        this.skill = skill;
        this.points = points;
    }

    public SkillRecord(ItemComponent<?> component) {
        super(component);
    }

    @Override
    public boolean revert(int level) {
        var interval = Configuration.instance().levelsPerSkillPoint;
        var change = this.component.level() / interval - level / interval;
        var deduction = Math.min(this.points, change);

        if (deduction > 0) {
            this.component.add(StatisticType.skillPoints, -deduction);
            this.skill().downgrade();
            this.points -= deduction;

            return this.points + deduction >= change;
        }

        return true;
    }

    @Override
    public void pop() {
        this.component.skill(this.skill).downgrade();
        this.component.add(StatisticType.skillPoints, -this.points);
    }

    @Override
    public void deserialize(NbtCompound tag) {
        this.skill = Skills.registry().getValue(new Identifier(tag.getString("skill")));
        this.points = tag.getInt("points");
    }

    @Override
    public void serialize(NbtCompound tag) {
        tag.putString("skill", this.skill.string());
        tag.putInt("points", this.points);
    }

    private SkillInstance skill() {
        return this.component.skill(this.skill);
    }
}
