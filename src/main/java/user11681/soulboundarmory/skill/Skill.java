package user11681.soulboundarmory.skill;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import user11681.soulboundarmory.util.Util;
import user11681.spun.client.gui.screen.SpunScreen;
import user11681.usersmanual.collections.ArraySet;

public abstract class Skill implements RegistryEntry {
    protected final Identifier identifier;
    protected final Identifier texture;

    protected ArraySet<Skill> dependencies;
    protected int maxLevel;
    protected int tier;
    public static final Registry<Skill> skill = Util.simpleRegistry("skill");

    public Skill(final Identifier identifier) {
        this(identifier, 0);
    }

    public Skill(final Identifier identifier, final int maxLevel) {
        this(identifier, new Identifier(identifier.getNamespace(), String.format("textures/skill/%s.png", identifier.getPath())), maxLevel);
    }

    public Skill(final Identifier identifier, final Identifier texture) {
        this(identifier, texture, 0);
    }

    public Skill(final Identifier identifier, final Identifier texture, final int maxLevel) {
        this.identifier = identifier;
        this.texture = texture;
        this.maxLevel = maxLevel;
        this.dependencies = new ArraySet<>();
    }

    /**
     * Register dependencies for this skill.<br>
     * Must be called <b><i>after</i></b> all skills are initialized and <b><i>at the ends</i></b> of any overriding methods.
     */
    public void initDependencies() {
        this.tier = this.hasDependencies() ? 1 : 0;

        for (final Skill dependency : this.getDependencies()) {
            final int previous = dependency.getTier();

            if (previous > 0) {
                ++this.tier;
            }
        }
    }

    public List<Skill> getDependencies() {
        return this.dependencies;
    }

    public boolean hasDependencies() {
        return !this.dependencies.isEmpty();
    }

    public int getTier() {
        return this.tier;
    }

    @Override
    public Identifier getIdentifier() {
        return this.identifier;
    }

    public abstract int getCost(final boolean learned, final int level);

    @Environment(EnvType.CLIENT)
    public Text getName() {
        final String name = I18n.translate(String.format("skill.%s.%s.name", this.identifier.getNamespace(), this.identifier.getPath()));
        final String first = name.substring(0, 1);

        return new LiteralText(name.replaceFirst("^.", first.toUpperCase()));
    }

    @Environment(EnvType.CLIENT)
        public List<String> getTooltip() {
        return Arrays.asList(I18n.translate(String.format("skill.%s.%s.desc", this.identifier.getNamespace(), this.identifier.getPath())).split("\\\\n"));
    }

    @Environment(EnvType.CLIENT)
    public void render(final SpunScreen screen, final MatrixStack matrices, final int level, final int x, final int y, final int zOffset) {
        SpunScreen.TEXTURE_MANAGER.bindTexture(this.getTexture());
        screen.withZ(zOffset, () -> Screen.drawTexture(matrices, x, y, 0, 0, 16, 16, 16, 16));
    }

    @Environment(EnvType.CLIENT)
    public Identifier getTexture() {
        return this.texture;
    }
}
