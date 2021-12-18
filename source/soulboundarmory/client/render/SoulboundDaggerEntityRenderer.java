package soulboundarmory.client.render;

import cell.client.gui.widget.Widget;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.Map;
import java.util.stream.IntStream;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import soulboundarmory.entity.SoulboundDaggerEntity;
import soulboundarmory.util.Util;

public class SoulboundDaggerEntityRenderer extends EntityRenderer<SoulboundDaggerEntity> {
    private static final Map<BakedModel, Identifier> ids = new Reference2ReferenceOpenHashMap<>();
    private static final Identifier id = Util.id("textures/item/dagger/0.png");

    public SoulboundDaggerEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public void vertex(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertexConsumer, int x, int y, int z, float u, float v, int normalX, int normalZ, int normalY, int light) {
        vertexConsumer.vertex(positionMatrix, x, y, z).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, normalX, normalY, normalZ).next();
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

        var j = false;
        var h = 0;
        var k = .5F;
        var l = 0;
        var m = .15625f;
        var n = 0;
        var o = .15625f;
        var p = .15625f;
        var q = .3125f;
        var r = .05625f;
        var s = (float) dagger.shake - tickDelta;

        if (s > 0) {
            var t = -MathHelper.sin(s * 3) * s;
            matrixes.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(t));
        }

        matrixes.scale(r, r, r);
        matrixes.translate(2, 0, 0);

        var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(this.getTexture(dagger)));
        var entry = matrixes.peek();
        var matrix4f = entry.getPositionMatrix();
        var matrix3f = entry.getNormalMatrix();

        this.vertex(matrix4f, matrix3f, vertexConsumer, -5, -2, -2, 0, 0.15625f, 1, 0, 0, light);
        this.vertex(matrix4f, matrix3f, vertexConsumer, -5, -2, 2, 0.15625f, 0.15625f, 1, 0, 0, light);
        this.vertex(matrix4f, matrix3f, vertexConsumer, -5, 2, 2, 0.15625f, 0.3125f, 1, 0, 0, light);
        this.vertex(matrix4f, matrix3f, vertexConsumer, -5, 2, -2, 0, 0.3125f, 1, 0, 0, light);
        this.vertex(matrix4f, matrix3f, vertexConsumer, -7, 2, -2, 0, 0.15625f, 1, 0, 0, light);
        this.vertex(matrix4f, matrix3f, vertexConsumer, -7, 2, 2, 0.15625f, 0.15625f, 1, 0, 0, light);
        this.vertex(matrix4f, matrix3f, vertexConsumer, -7, -2, 2, 0.15625f, 0.3125f, 1, 0, 0, light);
        this.vertex(matrix4f, matrix3f, vertexConsumer, -7, -2, -2, 0, 0.3125f, 1, 0, 0, light);

        IntStream.range(0, 4).forEach(__ -> {
            matrixes.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
            this.vertex(matrix4f, matrix3f, vertexConsumer, -8, -2, 0, 0, 0, 0, 1, 0, light);
            this.vertex(matrix4f, matrix3f, vertexConsumer, 8, -2, 0, k, 0, 0, 1, 0, light);
            this.vertex(matrix4f, matrix3f, vertexConsumer, 8, 2, 0, k, 0.15625f, 0, 1, 0, light);
            this.vertex(matrix4f, matrix3f, vertexConsumer, -8, 2, 0, 0, 0.15625f, 0, 1, 0, light);
        });

        matrixes.pop();

        super.render(dagger, yaw, tickDelta, matrixes, vertexConsumers, light);
    }
}
