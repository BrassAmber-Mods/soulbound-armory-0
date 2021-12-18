package soulboundarmory.client.render;

import cell.client.gui.widget.Widget;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.Map;
import java.util.stream.IntStream;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import soulboundarmory.entity.SoulboundDaggerEntity;
import soulboundarmory.util.Util;

public class SoulboundDaggerEntityRenderer extends EntityRenderer<SoulboundDaggerEntity> {
    private static final Map<BakedModel, Identifier> ids = new Reference2ReferenceOpenHashMap<>();
    private static final Identifier id = Util.id("textures/item/dagger/0.png");

    public SoulboundDaggerEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(SoulboundDaggerEntity entity) {
        return entity.component().map(component -> ids.computeIfAbsent(
            Widget.itemRenderer.getModel(component.stack(), entity.world, component.player, component.player.getId()),
            model -> {
                var id = model.getParticleSprite().getId();
                return new Identifier(id.getNamespace(), "textures/entity/%s.png".formatted(id.getPath().replaceFirst("^item/", "")));
            }
        )).orElse(id);
    }

    @Override
    public void render(SoulboundDaggerEntity dagger, float yaw, float tickDelta, MatrixStack matrixes, VertexConsumerProvider vertexConsumers, int light) {
        matrixes.push();
        matrixes.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.lerp(tickDelta, dagger.prevYaw, dagger.getYaw()) - 90));
        matrixes.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.lerp(tickDelta, dagger.prevPitch, dagger.getPitch())));

        var r = .05625F;
        var s = dagger.shake - tickDelta;

        if (s > 0) {
            matrixes.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-MathHelper.sin(s * 3) * s));
        }

        matrixes.scale(r, r, r);
        matrixes.translate(2, 0, 0);

        var entry = matrixes.peek();
        var matrix4f = entry.getPositionMatrix();
        var matrix3f = entry.getNormalMatrix();
        var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(this.getTexture(dagger)));

        var k = .5F;
        var m = .15625F;
        var o = .15625F;
        var p = .15625F;
        var q = .3125F;

        vertexConsumer.vertex(matrix4f, -5, -2, -2).color(255, 255, 255, 255).texture(0, .15625F).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 1, 0, 0).next();
        vertexConsumer.vertex(matrix4f, -5, -2, 2).color(255, 255, 255, 255).texture(.15625F, .15625F).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 1, 0, 0).next();
        vertexConsumer.vertex(matrix4f, -5, 2, 2).color(255, 255, 255, 255).texture(.15625F, q).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 1, 0, 0).next();
        vertexConsumer.vertex(matrix4f, -5, 2, -2).color(255, 255, 255, 255).texture(0, q).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 1, 0, 0).next();
        vertexConsumer.vertex(matrix4f, -7, 2, -2).color(255, 255, 255, 255).texture(0, .15625F).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 1, 0, 0).next();
        vertexConsumer.vertex(matrix4f, -7, 2, 2).color(255, 255, 255, 255).texture(.15625F, .15625F).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 1, 0, 0).next();
        vertexConsumer.vertex(matrix4f, -7, -2, 2).color(255, 255, 255, 255).texture(.15625F, q).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 1, 0, 0).next();
        vertexConsumer.vertex(matrix4f, -7, -2, -2).color(255, 255, 255, 255).texture(0, q).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 1, 0, 0).next();

        IntStream.range(0, 4).forEach(__ -> {
            matrixes.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
            vertexConsumer.vertex(matrix4f, -8, -2, 0).color(255, 255, 255, 255).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 0, 0, 1).next();
            vertexConsumer.vertex(matrix4f, 8, -2, 0).color(255, 255, 255, 255).texture(k, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 0, 0, 1).next();
            vertexConsumer.vertex(matrix4f, 8, 2, 0).color(255, 255, 255, 255).texture(k, .15625f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 0, 0, 1).next();
            vertexConsumer.vertex(matrix4f, -8, 2, 0).color(255, 255, 255, 255).texture(0, .15625f).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 0, 0, 1).next();
        });

        matrixes.pop();

        super.render(dagger, yaw, tickDelta, matrixes, vertexConsumers, light);
    }
}
