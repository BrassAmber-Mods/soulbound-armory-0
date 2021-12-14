package soulboundarmory.component.statistics;

import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import soulboundarmory.serial.Serializable;
import soulboundarmory.skill.Skill;
import soulboundarmory.skill.SkillContainer;

public class SkillStorage extends Reference2ObjectLinkedOpenHashMap<Skill, SkillContainer> implements Serializable {
    public void add(Skill... skills) {
        for (var skill : skills) {
            this.put(skill, new SkillContainer(skill));
        }

        this.values().forEach(container -> container.initializeDependencies(this));
    }

    public void reset() {
        this.values().forEach(SkillContainer::reset);
    }

    @Override
    public void serialize(NbtCompound tag) {
        for (var skill : this.values()) {
            if (skill != null) {
                tag.put(skill.skill.string(), skill.serialize());
            }
        }
    }

    @Override
    public void deserialize(NbtCompound tag) {
        for (var identifier : tag.getKeys()) {
            var skill = this.get(Skill.registry.getValue(new Identifier(identifier)));

            if (skill != null) {
                skill.deserialize(tag.getCompound(identifier));
            }
        }
    }
}
