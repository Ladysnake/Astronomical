package doctor4t.astronomical.client.render.entity;

import com.sammy.lodestone.handlers.RenderHandler;
import com.sammy.lodestone.setup.LodestoneRenderLayers;
import com.sammy.lodestone.systems.rendering.VFXBuilders;
import com.sammy.lodestone.systems.rendering.particle.Easing;
import com.sammy.lodestone.systems.rendering.particle.ParticleBuilders;
import doctor4t.astronomical.client.AstronomicalClient;
import doctor4t.astronomical.client.render.world.AstraSkyRenderer;
import doctor4t.astronomical.client.render.world.AstraWorldVFXBuilder;
import doctor4t.astronomical.client.render.world.VertexData;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.entity.FallenStarEntity;
import doctor4t.astronomical.common.init.ModParticles;
import doctor4t.astronomical.common.init.ModStatusEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.awt.*;

public class FallenStarEntityRenderer extends EntityRenderer<FallenStarEntity> {
	VFXBuilders.WorldVFXBuilder builder;

	public FallenStarEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
		this.builder = new AstraWorldVFXBuilder().setPosColorTexLightmapDefaultFormat();
	}

	@Override
	public void render(FallenStarEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		if (AstronomicalClient.renderStarsThisTick) {
			ParticleBuilders.create(ModParticles.FALLEN_STAR)
				.setScale((.8f + entity.world.random.nextFloat() / 3f))
				.setColor(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE)
				.setAlpha(0, 0.3f, 0)
				.enableNoClip()
				.setLifetime(20)
				.setSpinOffset(entity.world.random.nextFloat() * 360f)
				.setSpin((float) (entity.world.random.nextGaussian() / 100f))
				.spawn(entity.world, entity.getX(), entity.getY() + entity.getHeight() / 2f, entity.getZ());

			ParticleBuilders.create(ModParticles.STAR_IMPACT_FLARE)
				.setScale((6f + entity.world.random.nextFloat() * 5f))
				.setColor(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE)
				.setAlpha(0, 0.2f, 0)
				.enableNoClip()
				.setLifetime(20)
				.setSpinOffset(entity.world.random.nextFloat() * 360f)
				.setSpin((float) (entity.world.random.nextGaussian() / 100f))
				.spawn(entity.world, entity.getX(), entity.getY() + entity.getHeight() / 2f, entity.getZ());

			ParticleBuilders.create(ModParticles.FALLEN_STAR)
				.randomOffset(entity.world.random.nextFloat() * Math.signum(entity.world.random.nextGaussian()) * 10f)
				.setScale((.2f + entity.world.random.nextFloat() / 10f))
				.setColor(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE)
				.setAlpha(0, .5f, 0)
				.enableNoClip()
				.setLifetime(20 + entity.world.random.nextInt(20))
				.setSpinOffset(entity.world.random.nextFloat() * 360f)
				.setSpin((float) (entity.world.random.nextGaussian() / 50f))
				.spawn(entity.world, entity.getX(), entity.getY() + entity.getHeight() / 2f, entity.getZ());
		}

		if (!entity.getStack().isEmpty()) {
			float time = ((float) (MinecraftClient.getInstance().world.getTime() % 2400000L) + MinecraftClient.getInstance().getTickDelta());
			ItemStack visualStack = entity.getStack();
			visualStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("size", 1);
			matrices.push();
			matrices.translate(0, entity.getHeight() / 2f, 0);
			matrices.scale(.18f, .18f, .18f);
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(time));
			AstronomicalClient.renderAstralObject(matrices, vertexConsumers, builder, visualStack, 20, time, true);
			matrices.pop();
		}

		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	}

	@Override
	public Identifier getTexture(FallenStarEntity entity) {
		return AstraSkyRenderer.SHIMMER;
	}

	@Override
	public boolean shouldRender(FallenStarEntity entity, Frustum frustum, double x, double y, double z) {
		return true;
	}
}
