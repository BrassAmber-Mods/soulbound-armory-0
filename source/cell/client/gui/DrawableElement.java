package cell.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.Window;
import net.minecraft.resource.ResourceManager;

public interface DrawableElement extends Drawable, Element, TickableElement {
    // Needs to be named diffirently from Screen#client.
    MinecraftClient minecraft = MinecraftClient.getInstance();
    Window window = minecraft.getWindow();
    Mouse mouse = minecraft.mouse;
    TextRenderer textDrawer = minecraft.textRenderer;
    TextureManager textureManager = minecraft.textureManager;
    TextHandler textHandler = textDrawer.getTextHandler();
    ResourceManager resourceManager = minecraft.getResourceManager();
    ItemRenderer itemRenderer = minecraft.getItemRenderer();
    EntityRenderDispatcher entityRenderDispatcher = minecraft.getEntityRenderDispatcher();

    @Override
    default void tick() {}
}
