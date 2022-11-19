package soulboundarmory.client.render;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import soulboundarmory.entity.SoulboundDaggerEntity;
import soulboundarmory.module.gui.Node;
import soulboundarmory.util.Util;

public class SoulboundDaggerEntityRenderer extends EntityRenderer<SoulboundDaggerEntity> {
	private static final Identifier id = Util.id("textures/item/dagger/0.png");

	public SoulboundDaggerEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	@Override
	public Identifier getTexture(SoulboundDaggerEntity entity) {
		return entity.component().map(component -> Node.itemRenderer.getModel(component.stack(), entity.world, component.player, component.player.getId()).getParticleSprite().getId()).orElse(id);
	}

	@Override
	public void render(SoulboundDaggerEntity dagger, float yaw, float tickDelta, MatrixStack matrixes, VertexConsumerProvider vertexConsumers, int light) {
		matrixes.push();
		Util.rotate(matrixes, Vec3f.POSITIVE_Y, MathHelper.lerp(tickDelta, dagger.prevYaw, dagger.getYaw()) + 90);
		Util.rotate(matrixes, Vec3f.POSITIVE_Z, 45 - MathHelper.lerp(tickDelta, dagger.prevPitch, dagger.getPitch()));

		var shake = dagger.shake - tickDelta;

		if (shake > 0) {
			Util.rotate(matrixes, Vec3f.POSITIVE_Z, -MathHelper.sin(shake * 3) * shake);
		}

		var size = .75F;
		matrixes.scale(size, size, size);
		Node.client.getItemRenderer().renderItem(dagger.asItemStack(), ModelTransformation.Mode.FIXED, light, 0, matrixes, vertexConsumers, dagger.age);
		matrixes.pop();

		super.render(dagger, yaw, tickDelta, matrixes, vertexConsumers, light);
	}
}
