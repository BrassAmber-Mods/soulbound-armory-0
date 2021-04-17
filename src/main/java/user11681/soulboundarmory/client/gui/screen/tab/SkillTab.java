package user11681.soulboundarmory.client.gui.screen.tab;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import ;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.client.gui.RGBASlider;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.skill.Skill;
import user11681.soulboundarmory.skill.SkillContainer;
import user11681.spun.client.gui.screen.ScreenTab;
import user11681.spun.client.gui.widget.scalable.ScalableWidget;
import user11681.spun.client.gui.widget.scalable.WidgetSupplier;
import user11681.usersmanual.client.gui.screen.ScreenTab;
import user11681.usersmanual.client.gui.widget.scalable.ScalableWidget;
import user11681.usersmanual.client.gui.widget.scalable.WidgetSupplier;
import user11681.usersmanual.collections.ArrayMap;
import user11681.usersmanual.collections.OrderedArrayMap;

import static user11681.soulboundarmory.component.statistics.StatisticType.skillPoints;

@Environment(EnvType.CLIENT)
public class SkillTab extends SoulboundTab {
    protected static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/block/andesite.png");
    protected static final Identifier WINDOW = new Identifier("textures/gui/advancements/window.png");
    protected static final Identifier WIDGETS = new Identifier("textures/gui/advancements/widgets.png");

    protected final ArrayMap<SkillContainer, List<Integer>> skills;

    protected ScalableWidget window;
    protected SkillContainer selectedSkill;

    protected float chroma;

    protected int insideWidth;
    protected int insideHeight;
    protected int centerX;
    protected int centerY;
    protected int insideCenterX;
    protected int insideCenterY;
    protected int insideX;
    protected int insideY;
    protected int insideEndX;
    protected int insideEndY;
    protected int x;
    protected int y;

    public SkillTab(final SoulboundComponent component, final List<ScreenTab> tabs) {
        super(Translations.menuSkills, component, tabs);

        this.skills = new OrderedArrayMap<>();
    }

    @Override
    public void init(final MinecraftClient client, final int width, final int height) {
        super.init(client, width, height);

        this.chroma = 1;

        this.window = WidgetSupplier.WINDOW.get();
        this.window.setWidth(512);
        this.window.setHeight(288);
        this.centerX = Math.max(this.tab.getEndX() + this.window.getWidth() / 2 + 4, this.width / 2);
        this.centerY = Math.min(this.getXPBarY() - 16 - this.window.getHeight() / 2, this.height / 2);
        this.window.x = this.centerX - this.window.getWidth() / 2;
        this.window.y = this.centerY - this.window.getHeight() / 2;
        this.insideWidth = this.window.getWidth() - 18;
        this.insideHeight = this.window.getHeight() - 29;
        this.insideCenterX = this.centerX;
        this.insideCenterY = this.centerY + 4;
        this.insideX = this.insideCenterX - this.insideWidth / 2;
        this.insideY = this.insideCenterY - this.insideHeight / 2;
        this.insideEndX = this.centerX + this.insideWidth / 2;
        this.insideEndY = this.centerY + this.insideHeight / 2;

        this.updateIcons();
    }

    @Override
    protected boolean initSettings() {
        if (super.initSettings()) {
            final RGBASlider slider = this.sliders.get(0);

            if (slider != null && slider.x < this.x + this.window.getWidth()) {
                this.buttons.removeAll(this.options);
            }

            return true;
        }

        return false;
    }

    @Override
    public void render(final MatrixStack matrices, final int mouseX, final int mouseY, final float partialTicks) {
        super.render(matrices, mouseX, mouseY, partialTicks);

        this.renderWindow(matrices, mouseX, mouseY, partialTicks);
        this.renderSkills(matrices, mouseX, mouseY);
    }

    public void renderWindow(final MatrixStack stack, final int mouseX, final int mouseY, final float tickDelta) {
        this.setChroma(this.chroma);
        RenderSystem.enableBlend();

        this.renderBackground(BACKGROUND_TEXTURE, this.insideX, this.insideY, this.insideWidth, this.insideHeight, (int) (128 * this.chroma));

        this.window.render(stack, mouseX, mouseY, tickDelta);
        this.withZ(-200, () -> this.drawVerticallyInterpolatedTexture(stack, this.x, this.y, 0, 0, 22, 126, 140, this.window.getWidth(), this.window.getHeight()));

        final int color = 0x404040;
        final int points = this.storage.getDatum(skillPoints);

        TEXT_RENDERER.draw(stack, Translations.menuSkills, x + 8, y + 6, color);

        if (points > 0) {
            final Text text = new StringableText("%s: %s", Translations.menuUnspentPoints, points);

            TEXT_RENDERER.draw(stack, text, this.insideEndX - TEXT_RENDERER.getWidth(text), y + 6, color);
        }

        final float delta = 20F * tickDelta / 255F;

        this.chroma = this.isSkillSelected(mouseX, mouseY)
                ? Math.max(this.chroma - delta, 175F / 255F)
                : Math.min(this.chroma + delta, 1F);
    }

    protected void renderSkills(final MatrixStack stack, final int mouseX, final int mouseY) {
        final List<SkillContainer> skills = this.skills.keySet();

        for (int i = 0, skillsLength = skills.size(); i < skillsLength; i++) {
            final SkillContainer skill = skills.get(i);

            if (this.isMouseOverSkill(skill, mouseX, mouseY)) {
                this.selectedSkill = skill;
            } else {
                this.renderSkill(stack, skill, mouseX, mouseY);
            }

            if (i == skillsLength - 1 && this.selectedSkill != null) {
                this.renderSkill(stack, this.selectedSkill, mouseX, mouseY);
            }
        }
    }

    protected void renderSkill(final MatrixStack matrices, final SkillContainer skill, final int mouseX, final int mouseY) {
        final List<Integer> positions = this.skills.get(skill);
        final int width = 16;
        final int height = 16;
        final int x = positions.get(0) - width / 2;
        final int y = positions.get(1) - height / 2;
        final int offsetV = skill.isLearned() ? 26 : 0;
        final float chroma;

        if (skill == this.selectedSkill) {
            this.setChroma(1);

            if (this.isMouseOverSkill(skill, mouseX, mouseY)) {
                this.renderTooltip(matrices, skill, x, y, offsetV);
            }

            chroma = 1;
        } else {
            this.setChroma(this.chroma);
            this.addZ(-200);

            chroma = this.chroma;
        }

        TEXTURE_MANAGER.bindTexture(WIDGETS);
        this.drawTexture(matrices, x - 4, y - 4, 1, 155 - offsetV, 24, 24);

        RenderSystem.color3f(chroma, chroma, chroma);

        skill.render(this, matrices, x, y, this.getZOffset());

        this.setZOffset(0);
    }

    protected void renderTooltip(final MatrixStack stack, final SkillContainer skill, final int centerX, final int centerY, final int offsetV) {
        final Text name = skill.getName();
        List<String> tooltip = skill.getTooltip();
        int barWidth = 36 + TEXT_RENDERER.getWidth(name);
        int size = tooltip.size();

        if (size > 0) {
            final boolean learned = skill.isLearned();
            final boolean belowCenter = centerY > this.insideCenterY;
            final int y = centerY + (belowCenter ? -56 : 14);
            final int textY = y + 7;
            Text string = null;

            if (!learned) {
                final int cost = skill.getCost();
                final Text plural = cost > 1 ? Translations.menuPoints : Translations.menuPoint;
                string = new StringableText(Translations.menuSkillLearnCost.toString(), cost, plural);
            } else if (skill.canBeUpgraded()) {
                string = new StringableText("%s %s", Translations.menuLevel, skill.getLevel());
            }

            barWidth = 12 + Math.max(barWidth, 8 + TEXT_RENDERER.getWidth(string));
            tooltip = this.wrap(barWidth, tooltip.toArray(new String[0]));
            size = tooltip.size();
            barWidth = Math.max(barWidth, 8 + TEXT_RENDERER.getWidth(tooltip.stream().max(Comparator.comparingInt(String::length)).get()));
            final int offset = (1 + size) * TEXT_RENDERER.fontHeight;
            final int tooltipHeight = 1 + offset;

            if (!learned || skill.canBeUpgraded()) {
                this.setChroma(1);

                TEXTURE_MANAGER.bindTexture(WIDGETS);
                final int levelY = y + tooltipHeight;
                this.drawHorizontallyInterpolatedTexture(stack, centerX - 8, levelY, 0, 55, 2, 198, 200, barWidth, 20);

                TEXT_RENDERER.draw(stack, string, centerX - 3, textY + offset, 0x999999);
            }

            this.setChroma(1);

            TEXTURE_MANAGER.bindTexture(WIDGETS);
            this.drawInterpolatedTexture(stack, centerX - 8, y, 0, 55, 2, 57, 198, 73, 200, 75, barWidth, tooltipHeight);

            for (int i = 0; i < size; i++) {
                TEXT_RENDERER.draw(stack, tooltip.get(i), centerX - 3, textY - 1 + i * TEXT_RENDERER.fontHeight, 0x999999);
            }
        }

        this.setChroma(1);

        TEXTURE_MANAGER.bindTexture(WIDGETS);
        this.drawHorizontallyInterpolatedTexture(stack, centerX - 8, centerY - 2, 0, 29 - offsetV, 2, 198, 200, barWidth, 20);

        TEXT_RENDERER.draw(stack, name, centerX + 24, centerY + 4, 0xFFFFFF);
    }

    protected boolean isSkillSelected(final int mouseX, final int mouseY) {
        return this.getSelectedSkill(mouseX, mouseY) != null;
    }

    protected SkillContainer getSelectedSkill(final double mouseX, final double mouseY) {
        for (final SkillContainer skill : this.skills.keySet()) {
            if (this.isMouseOverSkill(skill, mouseX, mouseY)) {
                return skill;
            }
        }

        return null;
    }

    protected boolean isMouseOverSkill(final SkillContainer skill, final double mouseX, final double mouseY) {
        final List<Integer> positions = this.skills.get(skill);

        return Math.abs(positions.get(0) - mouseX) <= 12 && Math.abs(positions.get(1) - mouseY) <= 12;
    }

    protected void setChroma(final float chroma) {
        RenderSystem.color3f(chroma, chroma, chroma);
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        final SkillContainer skill = this.getSelectedSkill(mouseX, mouseY);

        if (skill != null) {
            this.storage.upgradeSkill(skill);
        }
        return false;
    }

    @Override
    public void refresh() {
        final float chroma = this.chroma;

        super.refresh();

        this.chroma = chroma;
    }

    protected void updateIcons() {
        this.skills.clear();

        final Map<Integer, List<Integer>> tierOrders = new LinkedHashMap<>();
        final List<SkillContainer> skills = this.storage.getSkills();

        for (final SkillContainer skill : skills) {
            final int tier = skill.getTier();
            final List<Integer> data = tierOrders.getOrDefault(tier, Arrays.asList(0, 0, 0));

            if (!tierOrders.containsValue(data)) {
                tierOrders.put(tier, data);
            }

            data.set(1, data.get(1) + 1);
        }

        int width = 2;

        for (final int tier : tierOrders.keySet()) {
            final int tiers = tierOrders.get(tier).get(1);

            if (tier != 0 && tiers != 0) {
                width += tiers - 1;
            }
        }

        for (final SkillContainer skill : skills) {
            final int tier = skill.getTier();
            final List<Integer> data = tierOrders.getOrDefault(tier, Arrays.asList(0, 0, 0));
            data.set(0, data.get(0) + 1);

            if (!tierOrders.containsValue(data)) {
                tierOrders.put(tier, data);
            }

            final int spacing = !skill.hasDependencies() ? width * 24 : 48;
            final int offset = (1 - data.get(1)) * spacing / 2;
            int x = offset + (data.get(0) - 1) * spacing;

            final List<Skill> dependencies = skill.getDependencies();

            if (skill.hasDependencies()) {
                int total = 0;

                for (final Skill other : dependencies) {
                    total += this.skills.get(this.storage.getSkill(other)).get(0);
                }

                x += total / dependencies.size();
            } else {
                x += this.centerX;
            }

            this.skills.put(skill, Arrays.asList(x, this.insideY + 24 + 32 * tier));
        }
    }
}
