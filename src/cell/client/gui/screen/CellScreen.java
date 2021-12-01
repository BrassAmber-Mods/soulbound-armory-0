package cell.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import cell.client.gui.CellElement;
import cell.client.gui.DrawableElement;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("unchecked")
@OnlyIn(Dist.CLIENT)
public abstract class CellScreen extends Screen implements DrawableElement {
    public final ReferenceArrayList<DrawableElement> elements = new ReferenceArrayList<>();

    protected CellScreen() {
        this(StringTextComponent.EMPTY);
    }

    protected CellScreen(ITextComponent title) {
        super(title);
    }

    public static List<ITextProperties> wrap(List<? extends ITextProperties> lines, int width) {
        return lines.stream().map(line -> textHandler.splitLines(line, width, Style.EMPTY)).flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    protected void init() {
        super.init();

        this.elements.clear();
    }

    @Override
    public void tick() {
        super.tick();

        this.elements.forEach(DrawableElement::tick);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        for (var element : this.elements) {
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
    public List<? extends IGuiEventListener> children() {
        return this.elements;
    }

    protected <T extends Widget> void removeButtons(T... buttons) {
        this.removeButtons(Arrays.asList(buttons));
    }

    protected void removeButtons(Collection<? extends Widget> buttons) {
        this.buttons.removeAll(buttons);
    }

    protected <T extends Widget> void removeButton(T button) {
        this.buttons.remove(button);
    }

    @Override
    protected <T extends Widget> T addButton(T button) {
        return super.addButton(button);
    }

    public void renderBackground(ResourceLocation identifier, int x, int y, int width, int height) {
        this.renderBackground(identifier, x, y, width, height, 64, 0);
    }

    public void renderBackground(ResourceLocation identifier, int x, int y, int width, int height, int chroma) {
        this.renderBackground(identifier, x, y, width, height, chroma, 0);
    }

    public void renderBackground(ResourceLocation identifier, int x, int y, int width, int height, int chroma, int alpha) {
        var tessellator = Tessellator.getInstance();
        var builder = tessellator.getBuilder();
        float f = 1 << 5;
        float endX = x + width;
        float endY = y + height;

        CellElement.textureManager.bind(identifier);
        RenderSystem.color4f(1, 1, 1, 1);

        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
        builder.vertex(x, endY, 0).color(chroma, chroma, chroma, 255).uv(0, endY / f + alpha).endVertex();
        builder.vertex(endX, endY, 0).color(chroma, chroma, chroma, 255).uv(endX / f, endY / f + alpha).endVertex();
        builder.vertex(endX, y, 0).color(chroma, chroma, chroma, 255).uv(endX / f, alpha).endVertex();
        builder.vertex(x, y, 0).color(chroma, chroma, chroma, 255).uv(0, alpha).endVertex();

        tessellator.end();
    }

    public void renderGuiItem(ItemStack itemStack, int x, int y, int z) {
        this.withZ(z, () -> this.itemRenderer.renderGuiItem(itemStack, x, y));
    }

    public void withZ(int z, Runnable runnable) {
        this.addZOffset(z);
        this.itemRenderer.blitOffset = this.getBlitOffset();
        runnable.run();
        this.addZOffset(-z);
        this.itemRenderer.blitOffset = this.getBlitOffset();
    }

    public void addZOffset(int z) {
        this.setBlitOffset(this.getBlitOffset() + z);
        this.itemRenderer.blitOffset += z;
    }
}
