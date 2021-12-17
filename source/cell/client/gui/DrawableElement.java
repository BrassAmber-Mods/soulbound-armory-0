package cell.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.Window;
import net.minecraft.resource.ResourceManager;

public interface DrawableElement extends Drawable, Element {
    MinecraftClient client = MinecraftClient.getInstance();
    Window window = client.getWindow();
    Mouse mouse = client.mouse;
    TextRenderer textRenderer = client.textRenderer;
    TextureManager textureManager = client.textureManager;
    TextHandler textHandler = textRenderer.getTextHandler();
    ResourceManager resourceManager = client.getResourceManager();
    ItemRenderer itemRenderer = client.getItemRenderer();
    EntityRenderDispatcher entityRenderDispatcher = client.getEntityRenderDispatcher();
    InGameHud hud = client.inGameHud;
    BakedModelManager bakedModelManager = client.getBakedModelManager();
    GameRenderer gameRenderer = client.gameRenderer;

    default void tick() {}
}
