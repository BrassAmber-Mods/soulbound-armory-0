package soulboundarmory.skill;

import cell.client.gui.screen.CellScreen;
import cell.client.gui.widget.Widget;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
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

    protected final Identifier texture;
    protected Set<Skill> dependencies = new ReferenceOpenHashSet<>();
    protected int maxLevel;

    private int tier = -1;

    public Skill(Identifier identifier, Identifier texture, int maxLevel) {
        super(identifier);

        this.texture = texture;
        this.maxLevel = maxLevel;
    }

    public Skill(Identifier identifier, int maxLevel) {
        this(identifier, new Identifier(identifier.getNamespace(), "textures/skill/%s.png".formatted(identifier.getPath())), maxLevel);
    }

    public Skill(Identifier identifier, Identifier texture) {
        this(identifier, texture, 0);
    }

    public Skill(Identifier identifier) {
        this(identifier, 0);
    }

    public Skill(String path) {
        this(Util.id(path), 0);
    }

    /**
     @param learned whether this skill has been learned
     @param level the next level
     @return the cost of unlocking this skill if it has not been learned or upgrading it to level `level` from the previous level.
     */
    public abstract int cost(boolean learned, int level);

    /**
     @return whether this skill has multiple levels.
     */
    public final boolean isTiered() {
        return this.cost(true, 1) >= 0;
    }

    public Set<Skill> dependencies() {
        return this.dependencies;
    }

    public boolean hasDependencies() {
        return !this.dependencies.isEmpty();
    }

    /**
     A skill's tier is the number of levels of dependencies that it has.

     @return this skill's tier.
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
    public void render(CellScreen screen, MatrixStack matrixes, int level, int x, int y, int zOffset) {
        Widget.bind(this.texture);
        DrawableHelper.drawTexture(matrixes, x, y, zOffset, 0, 0, 16, 16, 16, 16);
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
