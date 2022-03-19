package soulboundarmory.client.gui.screen;

import java.util.List;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.lib.gui.coordinate.Coordinate;
import soulboundarmory.lib.gui.util.Point;
import soulboundarmory.lib.gui.widget.TextWidget;
import soulboundarmory.lib.gui.widget.Widget;
import soulboundarmory.lib.gui.widget.scalable.ScalableWidget;
import soulboundarmory.skill.SkillContainer;

public class SkillWidget extends Widget<SkillWidget> {
    protected static final ScalableWidget<?> grayRectangle = new ScalableWidget<>().grayRectangle().z(500);
    protected static final ScalableWidget<?> blueRectangle = new ScalableWidget<>().blueRectangle().z(500);
    protected final ScalableWidget<?> whiteFrame = new ScalableWidget<>().whiteRectangle().width(24).height(24);
    protected final ScalableWidget<?> yellowFrame = new ScalableWidget<>().yellowRectangle().width(24).height(24);

    public ScalableWidget<?> frame;
    public SkillContainer skill;
    public Widget<?> tooltip;

    public SkillWidget(SkillContainer skill) {
        this.skill = skill;
        this.frame = this.add(skill.learned() ? this.yellowFrame : this.whiteFrame).z(100);
        this.tooltip = this.add(new Widget<>());
        this.width(24).height(24);
    }

    @Override protected void render() {
        var x = this.middleX() - 8;
        var y = this.middleY() - 8;
        var chroma = 1F;

        if (this.skill == this.tab().selectedSkill) {
            chroma(1);

            if (this.tab().isHovered(this.skill)) {
                this.renderTooltip();
            }
        } else {
            chroma(chroma = this.tab().chroma);
            this.tab().addZ(-300);
        }

        chroma(chroma);
        this.skill.render(this.tab(), this.matrixes, x, y);
        this.tab().z(0);
    }

    @Override protected void renderTooltip() {
        var cost = this.skill.cost();
        var genericSections = new ReferenceArrayList<Text>();

        if (this.skill.learned()) {
            if (this.skill.skill.isTiered()) {
                genericSections.add(Translations.guiLevel.format(this.skill.level()));

                if (this.skill.canUpgrade()) {
                    genericSections.add((cost == 1 ? Translations.guiSkillUpgradeCostSingular : Translations.guiSkillUpgradeCostPlural).format(cost));
                }
            }
        } else if (this.skill.dependenciesFulfilled()) {
            genericSections.add((cost == 1 ? Translations.guiSkillLearnCostSingular : Translations.guiSkillLearnCostPlural).format(cost));
        }

        var sections = new ReferenceArrayList<List<? extends StringVisitable>>();
        var name = this.skill.name();
        var barWidth = Math.max(36 + Math.max(108, textRenderer.getWidth(name)), 12 + genericSections.stream().peek(section -> sections.add(List.of(section))).mapToInt(textRenderer::getWidth).max().orElse(0));
        var tooltip = this.skill.tooltip();

        if (tooltip.size() > 0) {
            sections.add(0, tooltip = wrap(tooltip, barWidth - 8));
        }

        var height = 1 + (1 + tooltip.size()) * fontHeight();
        var y = this.y() - 4 > this.tab().insideCenterY ? -56 : -7;
        var textY = 7;

        for (var section : sections) {
            var text = new TextWidget().x(5).y(textY).color(0x999999);
            section.forEach(text::text);
            new ScalableWidget<>().grayRectangle().parent(this).x(-4).y(1, y).width(barWidth).height(height).with(text).render(this.matrixes);

            y += height;
            textY = 6;
            height = 20;
        }

        chroma(1);
        new ScalableWidget<>().blueRectangle().parent(this).x(-4).y(0.5).centerY().width(barWidth).height(20).with(new TextWidget().x(32).y(6).shadow().text(name)).render(this.matrixes);
    }

    private SkillTab tab() {
        return (SkillTab) this.parent.get();
    }
}
