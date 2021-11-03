package user11681.soulboundarmory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import user11681.cell.client.gui.CellElement;
import user11681.soulboundarmory.client.keyboard.ExperienceBarKeyBinding;
import user11681.soulboundarmory.client.keyboard.GUIKeyBinding;

@OnlyIn(Dist.CLIENT)
public class SoulboundArmoryClient {
    public static final MinecraftClient client = CellElement.client;

    public static final KeyBinding guiKeyBinding = new GUIKeyBinding();
    public static final KeyBinding toggleXPBarKeyBinding = new ExperienceBarKeyBinding();

    public static PlayerEntity player() {
        return client.player;
    }
}
