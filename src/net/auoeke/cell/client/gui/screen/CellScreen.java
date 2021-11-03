package net.auoeke.cell.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import net.auoeke.cell.client.gui.CellElement;
import net.auoeke.cell.client.gui.DrawableElement;

@SuppressWarnings("unchecked")
@OnlyIn(Dist.CLIENT)
public abstract class CellScreen extends Screen implements DrawableElement {
    public final ReferenceArrayList<DrawableElement> elements = new ReferenceArrayList<>();

    protected CellScreen() {
        this(LiteralText.EMPTY);
    }

    protected CellScreen(Text title) {
        super(title);
    }

    public static List<StringVisitable> wrap(List<? extends StringVisitable> lines, int width) {
        return lines.stream().map(line -> textHandler.wrapLines(line, width, Style.EMPTY)).flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    protected void init() {
        super.init();

        this.elements.clear();
    }

    @Override
    public void tick() {
        super.tick();

        for (DrawableElement element : this.elements) {
            element.tick();
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        for (DrawableElement element : this.elements) {
            element.render(matrices, mouseX, mouseY, delta);
        }
    }

    protected <T extends DrawableElement> T add(T element) {
        this.elements.add(element);

        return element;
    }

    protected void add(Collection<? extends DrawableElement> elements) {
        elements.forEach(this::add);
    }

    protected void add(DrawableElement... elements) {
        this.add(Arrays.asList(elements));
    }

    protected void remove(DrawableElement element) {
        this.elements.remove(element);
    }

    protected void remove(Collection<? extends DrawableElement> elements) {
        elements.forEach(this::remove);
    }

    @Override
    public List<? extends Element> children() {
        return this.elements;
    }

    protected <T extends ClickableWidget> void removeButtons(T... buttons) {
        this.removeButtons(Arrays.asList(buttons));
    }

    protected void removeButtons(Collection<? extends ClickableWidget> buttons) {
        this.buttons.removeAll(buttons);
    }

    protected <T extends ClickableWidget> void removeButton(T button) {
        this.buttons.remove(button);
    }

    @Override
    protected <T extends ClickableWidget> T addButton(T button) {
        return super.addButton(button);
    }

    public void renderBackground(Identifier identifier, int x, int y, int width, int height) {
        this.renderBackground(identifier, x, y, width, height, 64, 0);
    }

    public void renderBackground(Identifier identifier, int x, int y, int width, int height, int chroma) {
        this.renderBackground(identifier, x, y, width, height, chroma, 0);
    }

    public void renderBackground(Identifier identifier, int x, int y, int width, int height, int chroma, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        float f = 1 << 5;
        float endX = x + width;
        float endY = y + height;

        CellElement.textureManager.bindTexture(identifier);
        RenderSystem.color4f(1, 1, 1, 1);

        builder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        builder.vertex(x, endY, 0).color(chroma, chroma, chroma, 255).texture(0, endY / f + alpha).next();
        builder.vertex(endX, endY, 0).color(chroma, chroma, chroma, 255).texture(endX / f, endY / f + alpha).next();
        builder.vertex(endX, y, 0).color(chroma, chroma, chroma, 255).texture(endX / f, alpha).next();
        builder.vertex(x, y, 0).color(chroma, chroma, chroma, 255).texture(0, alpha).next();

        tessellator.draw();
    }

    public void renderGuiItem(ItemStack itemStack, int x, int y, int z) {
        this.withZ(z, () -> this.itemRenderer.renderGuiItemIcon(itemStack, x, y));
    }

    public void withZ(int z, Runnable runnable) {
        this.addZOffset(z);
        this.itemRenderer.zOffset = this.getZOffset();
        runnable.run();
        this.addZOffset(-z);
        this.itemRenderer.zOffset = this.getZOffset();
    }

    public void addZOffset(int z) {
        this.setZOffset(this.getZOffset() + z);
        this.itemRenderer.zOffset += z;
    }
}
