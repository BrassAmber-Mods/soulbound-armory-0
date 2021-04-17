package user11681.soulboundarmory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import user11681.soulboundarmory.client.keyboard.ExperienceBarKeyBinding;
import user11681.soulboundarmory.client.keyboard.GUIKeyBinding;
import user11681.soulboundarmory.client.render.SoulboundDaggerEntityRenderer;
import user11681.soulboundarmory.client.render.SoulboundFireballEntityRenderer;
import user11681.soulboundarmory.entity.EntityTypes;
import user11681.soulboundarmory.event.ClientEventHandlers;

@Environment(EnvType.CLIENT)
public class SoulboundArmoryClient implements ClientModInitializer {
    public static final MinecraftClient client = MinecraftClient.getInstance();

    public static final KeyBinding guiKeyBinding = new GUIKeyBinding();
    public static final KeyBinding toggleXPBarKeyBinding = new ExperienceBarKeyBinding();

    public static final ClientSidePacketRegistry packetRegistry = ClientSidePacketRegistry.INSTANCE;

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(guiKeyBinding);
        KeyBindingHelper.registerKeyBinding(toggleXPBarKeyBinding);

        EntityRendererRegistry.INSTANCE.register(EntityTypes.soulboundDagger, SoulboundDaggerEntityRenderer::new);
        EntityRendererRegistry.INSTANCE.register(EntityTypes.soulboundFireball, SoulboundFireballEntityRenderer::new);

        ScreenMouseEvents.allowMouseScroll(null).register(ClientEventHandlers::scroll);
    }

    public static PlayerEntity getPlayer() {
        return client.player;
    }
}
