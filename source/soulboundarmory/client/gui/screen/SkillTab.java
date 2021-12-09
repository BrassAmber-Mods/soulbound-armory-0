package soulboundarmory.client.gui.screen;

import cell.client.gui.widget.scalable.ScalableWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ReferenceLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceLinkedOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.skill.SkillContainer;

/**
 The skill tab; design (not code of course) blatantly copied from the advancement screen.
 */
public class SkillTab extends SoulboundTab {
    protected static final Identifier background = new Identifier("textures/block/andesite.png");
    protected static final Identifier windowID = new Identifier("textures/gui/advancements/window.png");
    protected static final Identifier widgets = new Identifier("textures/gui/advancements/widgets.png");

    protected static final ScalableWidget grayRectangle = new ScalableWidget().grayRectangle();
    protected static final ScalableWidget blueRectangle = new ScalableWidget().blueRectangle();

    protected final Map<SkillContainer, List<Integer>> skills = new Reference2ReferenceLinkedOpenHashMap<>();

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

    public SkillTab() {
        super(Translations.guiSkills);
    }

    @Override
    public void init() {
        super.init();

        this.chroma = 1;
        this.window = new ScalableWidget().window().width(512).height(288);
        this.centerX = Math.max(this.button.endX() + this.window.width() / 2 + 4, this.width / 2);
        this.centerY = Math.min(this.parent.xpBar.y() - 16 - this.window.height() / 2, this.height / 2);
        // this.window.x(this.centerX - this.window.width() / 2);
        // this.window.y(this.centerY - this.window.height() / 2);
        this.window.x(this.centerX).y(this.centerY).center(true);
        this.insideWidth = this.window.width() - 18;
        this.insideHeight = this.window.height() - 29;
        this.insideCenterX = this.centerX;
        this.insideCenterY = this.centerY + 4;
        this.insideX = this.insideCenterX - this.insideWidth / 2;
        this.insideY = this.insideCenterY - this.insideHeight / 2;
        this.insideEndX = this.centerX + this.insideWidth / 2;
        this.insideEndY = this.centerY + this.insideHeight / 2;

        this.updateIcons();

        if (!this.parent.options.isEmpty()) {
            var slider = this.parent.sliders.get(0);

            if (slider != null && slider.x < this.x + this.window.width()) {
                this.buttons.removeAll(this.parent.options);
            }
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.render(matrices, mouseX, mouseY, partialTicks);

        this.renderWindow(matrices, mouseX, mouseY, partialTicks);
        this.renderSkills(matrices, mouseX, mouseY);
    }

    public void renderWindow(MatrixStack stack, int mouseX, int mouseY, float tickDelta) {
        this.chroma(this.chroma);
        RenderSystem.enableBlend();

        this.renderBackground(background, this.insideX, this.insideY, this.insideWidth, this.insideHeight, (int) (128 * this.chroma));

        this.withZ(-200, () -> this.window.render(stack, mouseX, mouseY, tickDelta));
        // this.withZ(-200, () -> this.drawVerticallyInterpolatedTexture(stack, this.x, this.y, 0, 0, 22, 126, 140, this.window.getWidth(), this.window.getHeight()));

        this.textRenderer.draw(stack, Translations.guiSkills, this.insideX + 8, this.window.y() + 6, 0x808080);
        var text = this.pointText(this.parent.storage.intValue(StatisticType.skillPoints));
        this.textRenderer.draw(stack, text, this.insideEndX - 8 - this.textRenderer.getWidth(text), this.insideY + 6, 0x808080);

        var delta = 20F * tickDelta / 255F;
        this.chroma = this.isSkillSelected(mouseX, mouseY) ? Math.max(this.chroma - delta, 175F / 255F) : Math.min(this.chroma + delta, 1F);
    }

    protected void renderSkills(MatrixStack stack, int mouseX, int mouseY) {
        for (var skill : this.skills.keySet()) {
            if (this.isHovered(skill, mouseX, mouseY)) {
                this.selectedSkill = skill;
            } else {
                this.renderSkill(stack, skill, mouseX, mouseY);
            }
        }

        this.renderSkill(stack, this.selectedSkill, mouseX, mouseY);
    }

    protected void renderSkill(MatrixStack matrices, SkillContainer skill, int mouseX, int mouseY) {
        var positions = this.skills.get(skill);

        if (positions != null) {
            var width = 16;
            var height = 16;
            var x = positions.get(0) - width / 2;
            var y = positions.get(1) - height / 2;
            var offsetV = skill.learned() ? 26 : 0;
            float chroma;

            if (skill == this.selectedSkill) {
                this.chroma(1);

                if (this.isHovered(skill, mouseX, mouseY)) {
                    this.renderTooltip(matrices, skill, x, y, offsetV);
                }

                chroma = 1;
            } else {
                this.chroma(this.chroma);
                this.addZOffset(-200);

                chroma = this.chroma;
            }

            textureManager.bindTexture(widgets);
            this.drawTexture(matrices, x - 4, y - 4, 1, 155 - offsetV, 24, 24);

            RenderSystem.color3f(chroma, chroma, chroma);

            skill.render(this, matrices, x, y, this.getZOffset());

            this.setZOffset(0);
        }
    }

    protected void renderTooltip(MatrixStack stack, SkillContainer skill, int centerX, int centerY, int offsetV) {
        var name = skill.name();
        List<? extends StringVisitable> tooltip = skill.tooltip();
        var barWidth = 36 + this.textRenderer.getWidth(name);
        var size = tooltip.size();

        if (size > 0) {
            var learned = skill.learned();
            var belowCenter = centerY > this.insideCenterY;
            var y = centerY + (belowCenter ? -56 : 14);
            var textY = y + 7;
            Text string = null;

            if (!learned) {
                var cost = skill.cost();
                var plural = cost == 1 ? Translations.guiPoint : Translations.guiPoints;
                string = Translations.guiSkillLearnCost.format(cost, plural);
            } else if (skill.canUpgrade()) {
                string = Translations.guiLevel.format(skill.level());
            }

            barWidth = 12 + Math.max(barWidth, 8 + this.textRenderer.getWidth(string));
            tooltip = wrap(tooltip, barWidth);
            size = tooltip.size();
            barWidth = Math.max(barWidth, 8 + this.textRenderer.getWidth(tooltip.stream().max(Comparator.comparingInt(this.textRenderer::getWidth)).get()));
            var offset = (1 + size) * this.textRenderer.fontHeight;
            var tooltipHeight = 1 + offset;

            if (!learned || skill.canUpgrade()) {
                this.chroma(1);

                var levelY = y + tooltipHeight;
                grayRectangle.x(centerX - 8).y(levelY).width(barWidth).height(20).render(stack);
                // this.drawHorizontallyInterpolatedTexture(stack, centerX - 8, levelY, 0, 55, 2, 198, 200, barWidth, 20);

                this.textRenderer.drawWithShadow(stack, string, centerX - 3, textY + offset, 0x999999);
            }

            this.chroma(1);

            grayRectangle.x(centerX - 8).y(y).width(barWidth).height(tooltipHeight).render(stack);
            // this.drawInterpolatedTexture(stack, centerX - 8, y, 0, 55, 2, 57, 198, 73, 200, 75, barWidth, tooltipHeight);

            for (var i = 0; i < size; i++) {
                this.textRenderer.draw(stack, tooltip.get(i).getString(), centerX - 3, textY - 1 + i * this.textRenderer.fontHeight, 0x999999);
            }
        }

        this.chroma(1);

        // textureManager.bind(WIDGETS);
        blueRectangle.x(centerX - 8).y(centerY - 2).width(barWidth).height(20).render(stack);
        // this.drawHorizontallyInterpolatedTexture(stack, centerX - 8, centerY - 2, 0, 29 - offsetV, 2, 198, 200, barWidth, 20);

        this.textRenderer.drawWithShadow(stack, name, centerX + 24, centerY + 4, 0xFFFFFF);
    }

    protected boolean isSkillSelected(int mouseX, int mouseY) {
        return this.selectedSkill(mouseX, mouseY) != null;
    }

    protected SkillContainer selectedSkill(double mouseX, double mouseY) {
        for (var skill : this.skills.keySet()) {
            if (this.isHovered(skill, mouseX, mouseY)) {
                return skill;
            }
        }

        return null;
    }

    protected boolean isHovered(SkillContainer skill, double mouseX, double mouseY) {
        var positions = this.skills.get(skill);
        return Math.abs(positions.get(0) - mouseX) <= 12 && Math.abs(positions.get(1) - mouseY) <= 12;
    }

    protected void chroma(float chroma) {
        RenderSystem.color3f(chroma, chroma, chroma);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        var skill = this.selectedSkill(mouseX, mouseY);

        if (skill != null) {
            this.parent.storage.upgrade(skill);

            return true;
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void updateIcons() {
        this.skills.clear();

        var tierOrders = new Int2ReferenceLinkedOpenHashMap<int[]>();
        var skills = this.parent.storage.skills();

        for (var skill : skills) {
            var tier = skill.tier();
            var data = tierOrders.getOrDefault(tier, new int[]{0, 0, 0});

            if (!tierOrders.containsValue(data)) {
                tierOrders.put(tier, data);
            }

            data[1]++;
        }

        var width = 2;

        for (int tier : tierOrders.keySet()) {
            var tiers = tierOrders.get(tier)[1];

            if (tier != 0 && tiers != 0) {
                width += tiers - 1;
            }
        }

        for (var skill : skills) {
            var tier = skill.tier();
            var data = tierOrders.getOrDefault(tier, new int[]{0, 0, 0});
            data[0]++;

            if (!tierOrders.containsValue(data)) {
                tierOrders.put(tier, data);
            }

            var spacing = !skill.hasDependencies() ? width * 24 : 48;
            var offset = (1 - data[1]) * spacing / 2;
            var x = offset + (data[0] - 1) * spacing;

            if (skill.hasDependencies()) {
                var dependencies = skill.dependencies;
                var total = 0;

                for (var other : dependencies) {
                    total += this.skills.get(other).get(0);
                }

                x += total / dependencies.size();
            } else {
                x += this.centerX;
            }

            this.skills.put(skill, Arrays.asList(x, this.insideY + 24 + 32 * tier));
        }
    }
}
