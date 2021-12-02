package soulboundarmory.skill;

import cell.client.gui.screen.CellScreen;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;
import soulboundarmory.registry.RegistryEntry;
import soulboundarmory.text.Translation;
import soulboundarmory.util.Util;

public abstract class Skill extends RegistryEntry<Skill> {
    public static final IForgeRegistry<Skill> registry = Util.registry("skill");

    protected final ResourceLocation texture;

    protected Set<Skill> dependencies;
    protected int maxLevel;
    protected int tier;

    public Skill(ResourceLocation identifier) {
        this(identifier, 0);
    }

    public Skill(ResourceLocation identifier, int maxLevel) {
        this(identifier, new ResourceLocation(identifier.getNamespace(), String.format("textures/skill/%s.png", identifier.getPath())), maxLevel);
    }

    public Skill(ResourceLocation identifier, ResourceLocation texture) {
        this(identifier, texture, 0);
    }

    public Skill(ResourceLocation identifier, ResourceLocation texture, int maxLevel) {
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
    public ITextComponent name() {
        var name = I18n.format(String.format("skill.%s.%s.name", this.getRegistryName().getNamespace(), this.getRegistryName().getPath()));

        return new StringTextComponent(name.substring(0, 1).toUpperCase() + name.substring(1));
    }

    @OnlyIn(Dist.CLIENT)
    public List<? extends ITextComponent> tooltip() {
        return Stream.of(I18n.format("skill.%s.%s.desc".formatted(this.getRegistryName().getNamespace(), this.getRegistryName().getPath())).split("\\\\n")).map(Translation::new).toList();
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation texture() {
        return this.texture;
    }

    @OnlyIn(Dist.CLIENT)
    public void render(CellScreen screen, MatrixStack matrices, int level, int x, int y, int zOffset) {
        CellScreen.textureManager.bindTexture(this.texture());
        screen.withZ(zOffset, () -> AbstractGui.blit(matrices, x, y, 0, 0, 16, 16, 16, 16));
    }
}
