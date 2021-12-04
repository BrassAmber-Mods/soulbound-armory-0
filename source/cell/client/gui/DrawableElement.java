package cell.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.TextureTickListener;
import net.minecraft.resource.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface DrawableElement extends Drawable, Element, TextureTickListener {
    MinecraftClient minecraft = MinecraftClient.getInstance();
    TextRenderer textDrawer = minecraft.textRenderer;
    TextureManager textureManager = minecraft.textureManager;
    TextHandler textHandler = textDrawer.getTextHandler();
    ResourceManager resourceManager = minecraft.getResourceManager();

    @Override
    default void tick() {}
}
