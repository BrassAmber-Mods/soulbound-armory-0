package user11681.soulboundarmory.capability.statistics;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import user11681.soulboundarmory.serial.CompoundSerializable;
import user11681.soulboundarmory.skill.Skill;
import user11681.soulboundarmory.skill.SkillContainer;

public class SkillStorage extends Object2ObjectOpenHashMap<Skill, SkillContainer> implements CompoundSerializable {
    public SkillStorage(Skill... skills) {
        super();

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
    public void serializeNBT(CompoundNBT tag) {
        for (SkillContainer skill : this.values()) {
            if (skill != null) {
                tag.put(skill.skill().getRegistryName().toString(), skill.serializeNBT());
            }
        }
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        for (String identifier : tag.getAllKeys()) {
             SkillContainer skill = this.get(Skill.registry.getValue(new ResourceLocation(identifier)));

            if (skill != null) {
                skill.deserializeNBT(tag.getCompound(identifier));
            }
        }
    }
}
