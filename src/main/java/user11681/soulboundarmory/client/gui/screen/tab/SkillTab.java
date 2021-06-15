package user11681.soulboundarmory.client.gui.screen.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceLinkedOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import user11681.cell.client.gui.widget.Slider;
import user11681.cell.client.gui.widget.scalable.ScalableWidget;
import user11681.cell.client.gui.widget.scalable.ScalableWidgets;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.skill.Skill;
import user11681.soulboundarmory.skill.SkillContainer;
import user11681.soulboundarmory.text.Translation;

import static user11681.soulboundarmory.capability.statistics.StatisticType.skillPoints;

@OnlyIn(Dist.CLIENT)
public class SkillTab extends SoulboundTab {
    protected static final ResourceLocation background = new ResourceLocation("textures/block/andesite.png");
    protected static final ResourceLocation windowID = new ResourceLocation("textures/gui/advancements/window.png");
    protected static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/advancements/widgets.png");
    protected static final ScalableWidget grayRectangle = ScalableWidgets.grayRectangle();
    protected static final ScalableWidget blueRectangle = ScalableWidgets.blueRectangle();

    protected final Map<SkillContainer, List<Integer>> skills;

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

    public SkillTab(SoulboundCapability component, List<ScreenTab> tabs) {
        super(Translations.menuSkills, component, tabs);

        this.skills = new Reference2ReferenceLinkedOpenHashMap<>();
    }

    @Override
    public void init(Minecraft client, int width, int height) {
        super.init(client, width, height);

        this.chroma = 1;

        this.window = ScalableWidgets.window();
        this.window.width(512);
        this.window.height(288);
        this.centerX = Math.max(this.tab.endX() + this.window.width / 2 + 4, this.width / 2);
        this.centerY = Math.min(this.getXPBarY() - 16 - this.window.height / 2, this.height / 2);
        this.window.x = this.centerX - this.window.width / 2;
        this.window.y = this.centerY - this.window.height / 2;
        this.insideWidth = this.window.width - 18;
        this.insideHeight = this.window.height - 29;
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
             Slider slider = this.sliders.get(0);

            if (slider != null && slider.x < this.x + this.window.width) {
                this.buttons.removeAll(this.options);
            }

            return true;
        }

        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.render(matrices, mouseX, mouseY, partialTicks);

        this.renderWindow(matrices, mouseX, mouseY, partialTicks);
        this.renderSkills(matrices, mouseX, mouseY);
    }

    public void renderWindow(MatrixStack stack, int mouseX, int mouseY, float tickDelta) {
        this.setChroma(this.chroma);
        RenderSystem.enableBlend();

        this.renderBackground(background, this.insideX, this.insideY, this.insideWidth, this.insideHeight, (int) (128 * this.chroma));

        this.withZ(-200, () -> this.window.render(stack, mouseX, mouseY, tickDelta));
        // this.withZ(-200, () -> this.drawVerticallyInterpolatedTexture(stack, this.x, this.y, 0, 0, 22, 126, 140, this.window.getWidth(), this.window.getHeight()));

         int color = 0x404040;
         int points = this.storage.datum(skillPoints);

        font.draw(stack, Translations.menuSkills, x + 8, y + 6, color);

        if (points > 0) {
             ITextComponent text = new Translation("%s: %s", Translations.menuUnspentPoints, points);

            font.draw(stack, text, this.insideEndX - font.width(text), y + 6, color);
        }

         float delta = 20F * tickDelta / 255F;

        this.chroma = this.isSkillSelected(mouseX, mouseY)
                ? Math.max(this.chroma - delta, 175F / 255F)
                : Math.min(this.chroma + delta, 1F);
    }

    protected void renderSkills(MatrixStack stack, int mouseX, int mouseY) {
        for (SkillContainer skill : this.skills.keySet()) {
            if (this.isMouseOverSkill(skill, mouseX, mouseY)) {
                this.selectedSkill = skill;
            } else {
                this.renderSkill(stack, skill, mouseX, mouseY);
            }
        }

        this.renderSkill(stack, this.selectedSkill, mouseX, mouseY);
    }

    protected void renderSkill(MatrixStack matrices, SkillContainer skill, int mouseX, int mouseY) {
         List<Integer> positions = this.skills.get(skill);
         int width = 16;
         int height = 16;
         int x = positions.get(0) - width / 2;
         int y = positions.get(1) - height / 2;
         int offsetV = skill.learned() ? 26 : 0;
         float chroma;

        if (skill == this.selectedSkill) {
            this.setChroma(1);

            if (this.isMouseOverSkill(skill, mouseX, mouseY)) {
                this.renderTooltip(matrices, skill, x, y, offsetV);
            }

            chroma = 1;
        } else {
            this.setChroma(this.chroma);
            this.addZOffset(-200);

            chroma = this.chroma;
        }

        textureManager.bind(WIDGETS);
        this.blit(matrices, x - 4, y - 4, 1, 155 - offsetV, 24, 24);

        RenderSystem.color3f(chroma, chroma, chroma);

        skill.render(this, matrices, x, y, this.getBlitOffset());

        this.setBlitOffset(0);
    }

    protected void renderTooltip(MatrixStack stack, SkillContainer skill, int centerX, int centerY, int offsetV) {
        ITextComponent name = skill.name();
        List<String> tooltip = skill.tooltip();
        int barWidth = 36 + font.width(name);
        int size = tooltip.size();

        if (size > 0) {
            boolean learned = skill.learned();
            boolean belowCenter = centerY > this.insideCenterY;
            int y = centerY + (belowCenter ? -56 : 14);
            int textY = y + 7;
            ITextComponent string = null;

            if (!learned) {
                int cost = skill.cost();
                ITextComponent plural = cost > 1 ? Translations.menuPoints : Translations.menuPoint;
                string = new Translation(Translations.menuSkillLearnCost.toString(), cost, plural);
            } else if (skill.canUpgrade()) {
                string = new Translation("%s %s", Translations.menuLevel, skill.level());
            }

            barWidth = 12 + Math.max(barWidth, 8 + this.font.width(string));
            tooltip = wrap(tooltip, barWidth);
            size = tooltip.size();
            barWidth = Math.max(barWidth, 8 + this.font.width(tooltip.stream().max(Comparator.comparingInt(String::length)).get()));
            int offset = (1 + size) * this.font.lineHeight;
            int tooltipHeight = 1 + offset;

            if (!learned || skill.canUpgrade()) {
                this.setChroma(1);

                int levelY = y + tooltipHeight;
                grayRectangle.x(centerX - 8).y(levelY).width(barWidth).height(20);
                grayRectangle.render(stack);
                // this.drawHorizontallyInterpolatedTexture(stack, centerX - 8, levelY, 0, 55, 2, 198, 200, barWidth, 20);

                this.font.draw(stack, string, centerX - 3, textY + offset, 0x999999);
            }

            this.setChroma(1);

            grayRectangle.x(centerX - 8).y(y).width(barWidth).height(tooltipHeight);
            grayRectangle.render(stack);
            // this.drawInterpolatedTexture(stack, centerX - 8, y, 0, 55, 2, 57, 198, 73, 200, 75, barWidth, tooltipHeight);

            for (int i = 0; i < size; i++) {
                this.font.draw(stack, tooltip.get(i), centerX - 3, textY - 1 + i * this.font.lineHeight, 0x999999);
            }
        }

        this.setChroma(1);

        // textureManager.bind(WIDGETS);
        blueRectangle.x(centerX - 8).y(centerY - 2).width(barWidth).height(20).render(stack);
        // this.drawHorizontallyInterpolatedTexture(stack, centerX - 8, centerY - 2, 0, 29 - offsetV, 2, 198, 200, barWidth, 20);

        font.draw(stack, name, centerX + 24, centerY + 4, 0xFFFFFF);
    }

    protected boolean isSkillSelected(int mouseX, int mouseY) {
        return this.getSelectedSkill(mouseX, mouseY) != null;
    }

    protected SkillContainer getSelectedSkill(double mouseX, double mouseY) {
        for (SkillContainer skill : this.skills.keySet()) {
            if (this.isMouseOverSkill(skill, mouseX, mouseY)) {
                return skill;
            }
        }

        return null;
    }

    protected boolean isMouseOverSkill(SkillContainer skill, double mouseX, double mouseY) {
         List<Integer> positions = this.skills.get(skill);

        return Math.abs(positions.get(0) - mouseX) <= 12 && Math.abs(positions.get(1) - mouseY) <= 12;
    }

    protected void setChroma(float chroma) {
        RenderSystem.color3f(chroma, chroma, chroma);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

         SkillContainer skill = this.getSelectedSkill(mouseX, mouseY);

        if (skill != null) {
            this.storage.upgrade(skill);
        }
        return false;
    }

    @Override
    public void refresh() {
        float chroma = this.chroma;

        super.refresh();

        this.chroma = chroma;
    }

    protected void updateIcons() {
        this.skills.clear();

        Map<Integer, List<Integer>> tierOrders = new LinkedHashMap<>();
        List<SkillContainer> skills = this.storage.skills();

        for (SkillContainer skill : skills) {
            int tier = skill.tier();
            List<Integer> data = tierOrders.getOrDefault(tier, Arrays.asList(0, 0, 0));

            if (!tierOrders.containsValue(data)) {
                tierOrders.put(tier, data);
            }

            data.set(1, data.get(1) + 1);
        }

        int width = 2;

        for (int tier : tierOrders.keySet()) {
            int tiers = tierOrders.get(tier).get(1);

            if (tier != 0 && tiers != 0) {
                width += tiers - 1;
            }
        }

        for (SkillContainer skill : skills) {
            int tier = skill.tier();
            List<Integer> data = tierOrders.getOrDefault(tier, Arrays.asList(0, 0, 0));
            data.set(0, data.get(0) + 1);

            if (!tierOrders.containsValue(data)) {
                tierOrders.put(tier, data);
            }

            int spacing = !skill.hasDependencies() ? width * 24 : 48;
            int offset = (1 - data.get(1)) * spacing / 2;
            int x = offset + (data.get(0) - 1) * spacing;

            Set<Skill> dependencies = skill.dependencies();

            if (skill.hasDependencies()) {
                int total = 0;

                for (Skill other : dependencies) {
                    total += this.skills.get(this.storage.skill(other)).get(0);
                }

                x += total / dependencies.size();
            } else {
                x += this.centerX;
            }

            this.skills.put(skill, Arrays.asList(x, this.insideY + 24 + 32 * tier));
        }
    }
}
