package soulboundarmory.client.gui.screen;

import java.util.List;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.lib.gui.widget.GraphicWidget;
import soulboundarmory.lib.gui.widget.TextWidget;
import soulboundarmory.lib.gui.widget.Widget;
import soulboundarmory.lib.gui.widget.scalable.ScalableWidget;
import soulboundarmory.skill.SkillInstance;

public class SkillWidget extends Widget<SkillWidget> {
    private final ScalableWidget<?> frame = this.add(new ScalableWidget<>().width(24).height(24));
    private final SkillTab tab;
    private final SkillInstance skill;

    public SkillWidget(SkillTab tab, SkillInstance skill) {
        this.tab = tab;
        this.skill = skill;
        this.frame.add(new GraphicWidget(widget -> skill.render(widget, this.matrixes)).center().x(0.5).y(0.5).size(16));
        this.primaryAction(() -> tab.container().item().upgrade(skill));
    }

    @Override protected void render() {
        if (this.skill.learned()) {
            this.frame.yellowRectangle();
        } else {
            this.frame.whiteRectangle();
        }

        if (this.tab.focusedSkill == this) {
            if (!this.isFocused()) {
                this.tab.focusedSkill = null;
            }
        } else {
            chroma(this.tab.chroma);
        }
    }

    @Override protected void renderTooltip() {
        this.tab.focusedSkill = this;
        chroma(1);

        var cost = this.skill.cost();
        var genericSections = new ReferenceArrayList<Text>();

        if (this.skill.learned()) {
            if (this.skill.skill.isTiered()) {
                genericSections.add(Translations.guiLevel.format(this.skill.level()));

                if (this.skill.canUpgrade()) {
                    genericSections.add(Translations.guiSkillUpgradeCost.format(cost));
                }
            }
        } else if (this.skill.dependenciesFulfilled()) {
            genericSections.add(Translations.guiSkillLearnCost.format(cost));
        }

        var sections = new ReferenceArrayList<List<? extends StringVisitable>>();
        var name = this.skill.name();
        var barWidth = Math.max(36 + Math.max(108, textRenderer.getWidth(name)), 12 + genericSections.stream().peek(section -> sections.add(List.of(section))).mapToInt(textRenderer::getWidth).max().orElse(0));
        var tooltip = this.skill.tooltip();

        if (tooltip.size() > 0) {
            sections.add(0, tooltip = wrap(tooltip, barWidth - 8));
        }

        var height = 1 + (1 + tooltip.size()) * fontHeight();
        var y = this.y() - 4 > this.tab.insideCenterY ? -56 : -7;
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
}
