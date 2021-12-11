package cell.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.TextureTickListener;
import net.minecraft.resource.ResourceManager;

public interface DrawableElement extends Drawable, Element, TextureTickListener {
    // Needs to be named diffirently from Screen#client.
    MinecraftClient minecraft = MinecraftClient.getInstance();
    TextRenderer textDrawer = minecraft.textRenderer;
    TextureManager textureManager = minecraft.textureManager;
    TextHandler textHandler = textDrawer.getTextHandler();
    ResourceManager resourceManager = minecraft.getResourceManager();

    @Override
    default void tick() {}
}
