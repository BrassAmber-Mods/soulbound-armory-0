package user11681.soulboundarmory.component.statistics;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import nerdhub.cardinal.components.api.util.NbtSerializable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import user11681.soulboundarmory.skill.Skill;
import user11681.soulboundarmory.skill.SkillContainer;

public class SkillStorage extends Object2ObjectOpenHashMap<Skill, SkillContainer> implements NbtSerializable {
    public SkillStorage(final Skill... skills) {
        super();

        for (final Skill skill : skills) {
            this.put(skill, new SkillContainer(skill, this));
        }
    }

    public void add(SkillContainer skill) {
        this.put(skill.getSkill(), skill);
    }

    public boolean contains(final Skill skill) {
        final SkillContainer container = this.get(skill);

        return container != null && container.isLearned();
    }

    public boolean contains(final Skill skill, final int level) {
        final SkillContainer container = this.get(skill);

        return container != null && container.isLearned() && container.getLevel() >= level;
    }

    public void reset() {
        for (final SkillContainer container : this.values()) {
            if (container.hasDependencies())
                this.clear();
        }
    }

    public @NotNull NbtCompound toTag(final NbtCompound tag) {
        for (final SkillContainer skill : this.values()) {
            if (skill != null) {
                tag.put(skill.getSkill().getIdentifier().toString(), skill.toTag(new NbtCompound()));
            }
        }

        return tag;
    }

    @Override
    public void fromTag(final NbtCompound tag) {
        for (final String identifier : tag.getKeys()) {
            final SkillContainer skill = this.get(Skill.skill.get(new Identifier(identifier)));

            if (skill != null) {
                skill.fromTag(tag.getCompound(identifier));
            }
        }
    }
}
