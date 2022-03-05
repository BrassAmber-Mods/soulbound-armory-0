package soulboundarmory.skill;

import soulboundarmory.lib.gui.widget.Widget;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.IForgeRegistry;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.registry.RegistryEntry;
import soulboundarmory.util.Util;

/**
 An active or passive ability; locked by default and possibly having multiple levels and dependencies.
 */
@EventBusSubscriber(modid = SoulboundArmory.ID, bus = EventBusSubscriber.Bus.MOD)
public abstract class Skill extends RegistryEntry<Skill> {
    public static final IForgeRegistry<Skill> registry = Util.newRegistry("skill");

    public final int maxLevel;

    protected final Set<Skill> dependencies = new ReferenceOpenHashSet<>();
    protected final Identifier texture;

    private int tier = -1;

    public Skill(Identifier identifier, Identifier texture, int maxLevel) {
        super(identifier);

        this.texture = texture;
        this.maxLevel = maxLevel;
    }

    public Skill(Identifier identifier, int maxLevel) {
        this(identifier, new Identifier(identifier.getNamespace(), "textures/skill/%s.png".formatted(identifier.getPath())), maxLevel);
    }

    public Skill(String path, int maxLevel) {
        this(Util.id(path), maxLevel);
    }

    public Skill(String path) {
        this(path, -1);
    }

    /**
     @param level the next level; 1 is the level reached after unlocking; never <= 0.
     @return the cost of unlocking or upgrading this skill; never < 0
     */
    public abstract int cost(int level);

    /**
     @return whether this skill has multiple levels
     */
    public final boolean isTiered() {
        return this.maxLevel != 1;
    }

    public Set<Skill> dependencies() {
        return this.dependencies;
    }

    public boolean hasDependencies() {
        return !this.dependencies.isEmpty();
    }

    /**
     A skill's tier is the number of levels of dependencies that it has.

     @return this skill's tier
     */
    public final int tier() {
        return this.tier;
    }

    public Text name() {
        return Translations.skillName(this);
    }

    public List<? extends StringVisitable> tooltip() {
        return Translations.skillDescription(this);
    }

    /**
     Render an icon of this skill.
     */
    public void render(Widget<?> tab, int level, int x, int y) {
        Widget.shaderTexture(this.texture);
        DrawableHelper.drawTexture(tab.matrixes, x, y, tab.z(), 0, 0, 16, 16, 16, 16);
    }

    /**
     Register dependencies for this skill.
     Must be invoked <b>after</b> all skills are initialized and <b>after</b> adding dependencies in any overriding methods.
     */
    protected void initDependencies() {
        if (this.tier == -1) {
            this.tier = this.hasDependencies() ? 1 : 0;

            for (var dependency : this.dependencies()) {
                dependency.initDependencies();
                var previous = dependency.tier() + 1;

                if (previous > this.tier) {
                    this.tier = previous;
                }
            }
        }
    }

    /**
     Initialize skills' dependencies.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerSkills(RegistryEvent.Register<Skill> event) {
        event.getRegistry().forEach(Skill::initDependencies);
    }
}
