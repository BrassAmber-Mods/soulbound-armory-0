package soulboundarmory.skill;

import net.minecraftforge.registries.IForgeRegistry;
import soulboundarmory.module.transform.Register;
import soulboundarmory.module.transform.RegisterAll;
import soulboundarmory.module.transform.Registry;
import soulboundarmory.skill.tool.common.AbsorptionSkill;
import soulboundarmory.skill.tool.common.CircumspectionSkill;
import soulboundarmory.skill.tool.common.EnderPullSkill;
import soulboundarmory.skill.weapon.common.NourishmentSkill;
import soulboundarmory.skill.weapon.common.PrecisionSkill;
import soulboundarmory.skill.weapon.dagger.ReturnSkill;
import soulboundarmory.skill.weapon.dagger.ShadowCloneSkill;
import soulboundarmory.skill.weapon.dagger.SneakReturnSkill;
import soulboundarmory.skill.weapon.dagger.ThrowingSkill;
import soulboundarmory.skill.weapon.greatsword.FreezingSkill;
import soulboundarmory.skill.weapon.greatsword.LeapingSkill;
import soulboundarmory.skill.weapon.sword.SummonLightningSkill;

@RegisterAll(type = Skill.class, registry = "skill")
public class Skills {
    // public static final Skill ambidexterity = new AmbidexteritySkill(SoulboundArmory.id("ambidexterity"));
    @Register("absorption") public static final Skill absorption = new AbsorptionSkill();
    @Register("circumspection") public static final Skill circumspection = new CircumspectionSkill();
    @Register("ender_pull") public static final Skill enderPull = new EnderPullSkill();
    @Register("freezing") public static final Skill freezing = new FreezingSkill();
    @Register("leaping") public static final Skill leaping = new LeapingSkill();
    @Register("nourishment") public static final Skill nourishment = new NourishmentSkill();
    @Register("precision") public static final Skill precision = new PrecisionSkill();
    @Register("returning") public static final Skill returning = new ReturnSkill();
    @Register("shadow_clone") public static final Skill shadowClone = new ShadowCloneSkill();
    @Register("sneak_return") public static final Skill sneakReturn = new SneakReturnSkill();
    @Register("summon_lightning") public static final Skill summonLightning = new SummonLightningSkill();
    @Register("throwing") public static final Skill throwing = new ThrowingSkill();

    @Registry("skill") public static native IForgeRegistry<Skill> registry();
}
