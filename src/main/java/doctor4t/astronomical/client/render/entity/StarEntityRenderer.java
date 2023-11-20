package doctor4t.astronomical.client.render.entity;

import com.sammy.lodestone.systems.rendering.particle.ParticleBuilders;
import doctor4t.astronomical.common.entity.StarEntity;
import doctor4t.astronomical.common.init.ModParticles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class StarEntityRenderer extends EntityRenderer<StarEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/end_crystal/end_crystal.png");

	public StarEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
	}

	@Override
	public void render(StarEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);

		if (!MinecraftClient.getInstance().isPaused()) {
			if (entity.age % 10 == 0) {
				ParticleBuilders.create(ModParticles.ORB)
					.setScale((10f + entity.world.random.nextFloat() / 10f))
					.setAlpha(0, 1f, 0)
					.enableNoClip()
					.setLifetime(20)
					.setSpin(-entity.world.random.nextFloat() / 7f)
					.spawn(entity.world, entity.getX(), entity.getY() + entity.getHeight() / 2f, entity.getZ());
			}
		}
	}

	@Override
	public Identifier getTexture(StarEntity entity) {
		return TEXTURE;
	}

	@Override
	public boolean shouldRender(StarEntity entity, Frustum frustum, double x, double y, double z) {
		return true;
	}
}
