package soulboundarmory.skill;

import cell.client.gui.screen.CellScreen;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;
import soulboundarmory.registry.RegistryEntry;
import soulboundarmory.text.Translation;
import soulboundarmory.util.Util;

public abstract class Skill extends RegistryEntry<Skill> {
    public static final IForgeRegistry<Skill> registry = Util.registry("skill");

    protected final Identifier texture;

    protected Set<Skill> dependencies;
    protected int maxLevel;
    protected int tier;

    public Skill(Identifier identifier) {
        this(identifier, 0);
    }

    public Skill(Identifier identifier, int maxLevel) {
        this(identifier, new Identifier(identifier.getNamespace(), String.format("textures/skill/%s.png", identifier.getPath())), maxLevel);
    }

    public Skill(Identifier identifier, Identifier texture) {
        this(identifier, texture, 0);
    }

    public Skill(Identifier identifier, Identifier texture, int maxLevel) {
        this.texture = texture;
        this.maxLevel = maxLevel;
        this.dependencies = new HashSet<>();

        this.setRegistryName(identifier);
    }

    public abstract int cost(boolean learned, int level);

    /**
     * Register dependencies for this skill.<br>
     * Must be called <b><i>after</i></b> all skills are initialized and <b><i>at the ends</i></b> of any overriding methods.
     */
    public void initDependencies() {
        this.tier = this.hasDependencies() ? 1 : 0;

        for (var dependency : this.dependencies()) {
            var previous = dependency.tier();

            if (previous > 0) {
                ++this.tier;
            }
        }
    }

    public Set<Skill> dependencies() {
        return this.dependencies;
    }

    public boolean hasDependencies() {
        return !this.dependencies.isEmpty();
    }

    public int tier() {
        return this.tier;
    }

    @OnlyIn(Dist.CLIENT)
    public Text name() {
        var name = I18n.translate(String.format("skill.%s.%s.name", this.getRegistryName().getNamespace(), this.getRegistryName().getPath()));

        return Text.of(name.substring(0, 1).toUpperCase() + name.substring(1));
    }

    @OnlyIn(Dist.CLIENT)
    public List<? extends Text> tooltip() {
        return Stream.of(I18n.translate("skill.%s.%s.desc".formatted(this.getRegistryName().getNamespace(), this.getRegistryName().getPath())).split("\\\\n")).map(Translation::new).toList();
    }

    @OnlyIn(Dist.CLIENT)
    public Identifier texture() {
        return this.texture;
    }

    @OnlyIn(Dist.CLIENT)
    public void render(CellScreen screen, MatrixStack matrices, int level, int x, int y, int zOffset) {
        CellScreen.textureManager.bindTexture(this.texture());
        screen.withZ(zOffset, () -> DrawableHelper.drawTexture(matrices, x, y, 0, 0, 16, 16, 16, 16));
    }
}
