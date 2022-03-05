package soulboundarmory.client.gui.screen;

import soulboundarmory.lib.gui.widget.scalable.ScalableWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ReferenceLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.skill.SkillContainer;

/**
 The skill tab; design (not code of course) blatantly copied from the advancement screen.
 */
public class SkillTab extends SoulboundTab {
    protected static final Identifier background = new Identifier("textures/block/andesite.png");

    protected static final ScalableWidget<?> grayRectangle = new ScalableWidget<>().grayRectangle().z(500);
    protected static final ScalableWidget<?> blueRectangle = new ScalableWidget<>().blueRectangle().z(500);
    protected static final ScalableWidget<?> whiteFrame = new ScalableWidget<>().whiteRectangle().width(24).height(24);
    protected static final ScalableWidget<?> yellowFrame = new ScalableWidget<>().yellowRectangle().width(24).height(24);

    protected final Map<SkillContainer, int[]> skills = new Reference2ReferenceLinkedOpenHashMap<>();

    protected ScalableWidget<?> window = new ScalableWidget<>().window().width(512).height(288).z(-1000);
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

    public SkillTab() {
        super(Translations.guiSkills);
    }

    protected static void chroma(float chroma) {
        RenderSystem.setShaderColor(chroma, chroma, chroma, -1);
    }

    @Override
    public void initialize() {
        this.chroma = 1;
        this.centerX = Math.max(this.button.endX() + this.window.width() / 2 + 4, this.middleX());
        this.centerY = Math.min(this.container().xpBar.y() - 16 - this.window.height() / 2, this.middleY());
        this.window.x(this.centerX).y(this.centerY).center();
        this.insideWidth = this.window.width() - 18;
        this.insideHeight = this.window.height() - 29;
        this.insideCenterX = this.centerX;
        this.insideCenterY = this.centerY + 4;
        this.insideX = this.insideCenterX - this.insideWidth / 2;
        this.insideY = this.insideCenterY - this.insideHeight / 2;
        this.insideEndX = this.centerX + this.insideWidth / 2;
        this.insideEndY = this.centerY + this.insideHeight / 2;

        this.add(this.window);
        this.updateIcons();

        if (!this.container().options.isEmpty()) {
            var slider = this.container().sliders.get(0);

            if (slider != null && slider.x() < this.window.endX()) {
                this.remove(this.container().options);
            }
        }
    }

    @Override
    protected void render() {
        this.renderWindow();
        this.renderSkills();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }

        var skill = this.selectedSkill();

        if (skill != null) {
            this.container().item.upgrade(skill);

            return true;
        }

        return false;
    }

    public void renderWindow() {
        chroma(this.chroma);
        RenderSystem.enableBlend();

        this.renderBackground(background, this.insideX, this.insideY, this.insideWidth, this.insideHeight, (int) (128 * this.chroma));

        drawStrokedText(this.matrixes, Translations.guiSkills, this.insideX + 8, this.window.y() + 6, 0xEEEEEE);
        var text = this.pointText(this.container().item.skillPoints());
        drawStrokedText(this.matrixes, text, this.insideEndX - 8 - textRenderer.getWidth(text), this.insideY + 6, 0xEEEEEE);

        var delta = 20F * tickDelta() / 255F;
        this.chroma = this.selectedSkill() == null ? Math.min(this.chroma + delta, 1F) : Math.max(this.chroma - delta, 175F / 255F);
    }

    protected void renderSkills() {
        for (var skill : this.skills.keySet()) {
            if (this.isHovered(skill)) {
                this.selectedSkill = skill;
            } else {
                this.renderSkill(skill);
            }
        }

        this.renderSkill(this.selectedSkill);
    }

    protected void renderSkill(SkillContainer skill) {
        var positions = this.skills.get(skill);

        if (positions != null) {
            var width = 16;
            var height = 16;
            var x = positions[0] - width / 2;
            var y = positions[1] - height / 2;
            float chroma;

            if (skill == this.selectedSkill) {
                chroma(1);

                if (this.isHovered(skill)) {
                    this.renderTooltip(this.matrixes, skill, x, y);
                }

                chroma = 1;
            } else {
                chroma(this.chroma);
                this.addZ(-300);

                chroma = this.chroma;
            }

            (skill.learned() ? yellowFrame : whiteFrame).x(x - 4).y(y - 4).z(200).render(this.matrixes);

            chroma(chroma);
            skill.render(this, this.matrixes, x, y);
            this.z(0);
        }
    }

    protected void renderTooltip(MatrixStack stack, SkillContainer skill, int centerX, int centerY) {
        var name = skill.name();
        var tooltip = skill.tooltip();
        var barWidth = 36 + Math.max(108, textRenderer.getWidth(name));

        var belowCenter = centerY > this.insideCenterY;
        var y = centerY + (belowCenter ? -56 : 14);
        var textY = y + 7;
        var cost = skill.cost();

        var sections = new ReferenceArrayList<List<? extends StringVisitable>>();
        var genericSections = new ReferenceArrayList<Text>();

        if (skill.learned()) {
            if (skill.skill.isTiered()) {
                genericSections.add(Translations.guiLevel.format(skill.level()));

                if (skill.canUpgrade()) {
                    genericSections.add((cost == 1 ? Translations.guiSkillUpgradeCostSingular : Translations.guiSkillUpgradeCostPlural).format(cost));
                }
            }
        } else if (skill.dependenciesFulfilled()) {
            genericSections.add((cost == 1 ? Translations.guiSkillLearnCostSingular : Translations.guiSkillLearnCostPlural).format(cost));
        }

        barWidth = Math.max(barWidth, 12 + genericSections.stream().peek(section -> sections.add(List.of(section))).map(textRenderer::getWidth).max(Comparator.naturalOrder()).orElse(0));

        if (tooltip.size() > 0) {
            sections.add(0, tooltip = wrap(tooltip, barWidth - 8));
        }

        var height = 1 + (1 + tooltip.size()) * fontHeight();

        for (var section : sections) {
            grayRectangle.x(centerX - 8).y(y).width(barWidth).height(height).render(stack);

            for (var line : section) {
                var why = textY;
                this.withZ(() -> textRenderer.draw(stack, line.getString(), centerX - 3, why, 0x999999));
                textY += fontHeight();
            }

            y += height;
            textY = y + 6;
            height = 20;
        }

        chroma(1);
        blueRectangle.x(centerX - 8).y(centerY - 2).width(barWidth).height(20).render(stack);
        this.withZ(() -> textRenderer.drawWithShadow(stack, name, centerX + 24, centerY + 4, 0xFFFFFF));
    }

    protected SkillContainer selectedSkill() {
        for (var skill : this.skills.keySet()) {
            if (this.isHovered(skill)) {
                return skill;
            }
        }

        return null;
    }

    protected boolean isHovered(SkillContainer skill) {
        var positions = this.skills.get(skill);
        return Math.abs(positions[0] - mouseX()) <= 12 && Math.abs(positions[1] - mouseY()) <= 12;
    }

    protected void updateIcons() {
        this.skills.clear();

        var tierOrders = new Int2ReferenceLinkedOpenHashMap<int[]>();
        var skills = this.container().item.skills();

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
                    total += this.skills.get(other)[0];
                }

                x += total / dependencies.size();
            } else {
                x += this.centerX;
            }

            this.skills.put(skill, new int[]{x, this.insideY + 24 + 32 * tier});
        }
    }
}
