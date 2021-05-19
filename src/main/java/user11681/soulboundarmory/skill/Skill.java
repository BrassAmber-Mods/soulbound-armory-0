package user11681.soulboundarmory.skill;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import user11681.soulboundarmory.util.Util;

public abstract class Skill extends ForgeRegistryEntry<Skill> {
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

        registry.register(this);
    }

    /**
     * Register dependencies for this skill.<br>
     * Must be called <b><i>after</i></b> all skills are initialized and <b><i>at the ends</i></b> of any overriding methods.
     */
    public void initDependencies() {
        this.tier = this.hasDependencies() ? 1 : 0;

        for (Skill dependency : this.dependencies()) {
            final int previous = dependency.tier();

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

    public abstract int cost(boolean learned, int level);

    @OnlyIn(Dist.CLIENT)
    public ITextComponent name() {
        String name = I18n.get(String.format("skill.%s.%s.name", this.getRegistryName().getNamespace(), this.getRegistryName().getPath()));

        return new StringTextComponent(name.substring(0, 1).toUpperCase() + name.substring(1));
    }

    @OnlyIn(Dist.CLIENT)
        public List<String> tooltip() {

        return Arrays.asList(I18n.get(String.format("skill.%s.%s.desc", this.getRegistryName().getNamespace(), this.getRegistryName().getPath())).split("\\\\n"));
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation texture() {
        return this.texture;
    }

    @OnlyIn(Dist.CLIENT)
    public void render(SpunScreen screen, MatrixStack matrices, int level, int x, int y, int zOffset) {
        SpunScreen.TEXTURE_MANAGER.bindTexture(this.texture());
        screen.withZ(zOffset, () -> AbstractGui.blit(matrices, x, y, 0, 0, 16, 16, 16, 16));
    }
}
