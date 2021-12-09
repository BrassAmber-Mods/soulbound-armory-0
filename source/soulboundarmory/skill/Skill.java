package soulboundarmory.skill;

import cell.client.gui.screen.CellScreen;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.IForgeRegistry;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.registry.RegistryEntry;
import soulboundarmory.text.Translation;
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

    public Skill(Identifier identifier) {
        this(identifier, 0);
    }

    public Skill(Identifier identifier, int maxLevel) {
        this(identifier, new Identifier(identifier.getNamespace(), "textures/skill/%s.png".formatted(identifier.getPath())), maxLevel);
    }

    public Skill(Identifier identifier, Identifier texture) {
        this(identifier, texture, 0);
    }

    public Skill(Identifier identifier, Identifier texture, int maxLevel) {
        super(identifier);

        this.texture = texture;
        this.maxLevel = maxLevel;
    }

    public abstract int cost(boolean learned, int level);

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
        var name = I18n.translate("skill.%s.%s.name".formatted(this.getRegistryName().getNamespace(), this.getRegistryName().getPath()));
        return Text.of(name.substring(0, 1).toUpperCase() + name.substring(1));
    }

    public List<? extends Text> tooltip() {
        return Stream.of(I18n.translate("skill.%s.%s.desc".formatted(this.getRegistryName().getNamespace(), this.getRegistryName().getPath())).split("\\\\n")).map(Translation::new).toList();
    }

    /**
     Render an icon of this skill.
     */
    public void render(CellScreen screen, MatrixStack matrixes, int level, int x, int y, int zOffset) {
        CellScreen.textureManager.bindTexture(this.texture);
        screen.withZ(zOffset, () -> DrawableHelper.drawTexture(matrixes, x, y, 0, 0, 16, 16, 16, 16));
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
