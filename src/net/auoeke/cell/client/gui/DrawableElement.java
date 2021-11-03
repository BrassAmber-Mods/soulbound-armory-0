package net.auoeke.cell.client.gui;

import net.minecraft.client.font.TextHandler;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Tickable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface DrawableElement extends Drawable, Element, Tickable {
    TextureManager textureManager = CellElement.client.textureManager;
    TextHandler textHandler = CellElement.textRenderer.getTextHandler();
    ResourceManager resourceManager = CellElement.client.getResourceManager();

    @Override
    default void tick() {}
}
