package user11681.soulboundarmory.client.renderer.texture;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ExperienceBarTexture extends DynamicTexture {
    public ExperienceBarTexture(NativeImage image) {
        super(image);
    }
}
