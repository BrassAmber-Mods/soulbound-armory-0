package net.auoeke.cell.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.text.CharacterManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface DrawableElement extends IRenderable, IGuiEventListener, ITickable {
    Minecraft client = Minecraft.getInstance();
    FontRenderer textRenderer = client.font;
    TextureManager textureManager = client.textureManager;
    CharacterManager textHandler = textRenderer.getSplitter();
    IResourceManager resourceManager = client.getResourceManager();

    @Override
    default void tick() {}
}
