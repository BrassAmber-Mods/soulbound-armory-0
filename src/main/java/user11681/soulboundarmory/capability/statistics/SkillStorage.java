package user11681.soulboundarmory.capability.statistics;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import nerdhub.cardinal.components.api.util.INBTSerializable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Identifier;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import user11681.soulboundarmory.skill.Skill;
import user11681.soulboundarmory.skill.SkillContainer;

public class SkillStorage extends Object2ObjectOpenHashMap<Skill, SkillContainer> implements INBTSerializable {
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
        final SkillContainer container = this.get(skill);

        return container != null && container.learned();
    }

    public boolean contains(Skill skill, final int level) {
        final SkillContainer container = this.get(skill);

        return container != null && container.learned() && container.level() >= level;
    }

    public void reset() {
        for (SkillContainer container : this.values()) {
            if (container.hasDependencies())
                this.clear();
        }
    }

    public CompoundNBT toTag(CompoundNBT tag) {
        for (SkillContainer skill : this.values()) {
            if (skill != null) {
                tag.put(skill.skill().getRegistryName().toString(), skill.toTag(new CompoundNBT()));
            }
        }

        return tag;
    }

    @Override
    public void fromTag(CompoundNBT tag) {
        for (String identifier : tag.getKeys()) {
            final SkillContainer skill = this.get(Skill.registry.get(new ResourceLocation(identifier)));

            if (skill != null) {
                skill.fromTag(tag.getCompound(identifier));
            }
        }
    }
}
