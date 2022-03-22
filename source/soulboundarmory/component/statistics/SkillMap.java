package soulboundarmory.component.statistics;

import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.statistics.history.SkillHistory;
import soulboundarmory.serial.Serializable;
import soulboundarmory.skill.Skill;
import soulboundarmory.skill.SkillInstance;
import soulboundarmory.skill.Skills;

public class SkillMap extends Reference2ObjectLinkedOpenHashMap<Skill, SkillInstance> implements Serializable {
    public SkillHistory history;

    public SkillMap(ItemComponent<?> component) {
        this.history = new SkillHistory(component);
    }

    public void add(Skill... skills) {
        for (var skill : skills) {
            this.put(skill, new SkillInstance(skill));
        }

        this.values().forEach(container -> container.initializeDependencies(this));
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
            var skill = this.get(Skills.registry().getValue(new Identifier(identifier)));

            if (skill != null) {
                skill.deserialize(tag.getCompound(identifier));
            }
        }
    }
}
