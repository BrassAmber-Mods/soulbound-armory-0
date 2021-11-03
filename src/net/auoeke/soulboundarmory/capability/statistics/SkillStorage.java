package net.auoeke.soulboundarmory.capability.statistics;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.auoeke.soulboundarmory.serial.CompoundSerializable;
import net.auoeke.soulboundarmory.skill.Skill;
import net.auoeke.soulboundarmory.skill.SkillContainer;

public class SkillStorage extends Object2ObjectLinkedOpenHashMap<Skill, SkillContainer> implements CompoundSerializable {
    public SkillStorage(Skill... skills) {
        for (Skill skill : skills) {
            this.put(skill, new SkillContainer(skill, this));
        }
    }

    public void add(SkillContainer skill) {
        this.put(skill.skill(), skill);
    }

    public boolean contains(Skill skill) {
        SkillContainer container = this.get(skill);

        return container != null && container.learned();
    }

    public boolean contains(Skill skill, int level) {
        SkillContainer container = this.get(skill);

        return container != null && container.learned() && container.level() >= level;
    }

    public void reset() {
        for (SkillContainer container : this.values()) {
            if (container.hasDependencies())
                this.clear();
        }
    }

    @Override
    public void serializeNBT(NbtCompound tag) {
        for (SkillContainer skill : this.values()) {
            if (skill != null) {
                tag.put(skill.skill().getRegistryName().toString(), skill.serializeNBT());
            }
        }
    }

    @Override
    public void deserializeNBT(NbtCompound tag) {
        for (String identifier : tag.getKeys()) {
            SkillContainer skill = this.get(Skill.registry.getValue(new Identifier(identifier)));

            if (skill != null) {
                skill.deserializeNBT(tag.getCompound(identifier));
            }
        }
    }
}
