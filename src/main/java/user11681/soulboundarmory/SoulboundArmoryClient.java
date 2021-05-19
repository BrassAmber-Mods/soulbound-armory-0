package user11681.soulboundarmory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import user11681.soulboundarmory.client.keyboard.ExperienceBarKeyBinding;
import user11681.soulboundarmory.client.keyboard.GUIKeyBinding;

@OnlyIn(Dist.CLIENT)
public class SoulboundArmoryClient {
    public static final Minecraft client = Minecraft.getInstance();

    public static final KeyBinding guiKeyBinding = new GUIKeyBinding();
    public static final KeyBinding toggleXPBarKeyBinding = new ExperienceBarKeyBinding();

    public static PlayerEntity player() {
        return client.player;
    }
}
