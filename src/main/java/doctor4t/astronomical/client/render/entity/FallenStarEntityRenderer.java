package doctor4t.astronomical.client.render.entity;

import doctor4t.astronomical.client.AstronomicalClient;
import doctor4t.astronomical.client.render.world.AstraSkyRenderer;
import doctor4t.astronomical.client.render.world.AstraWorldVFXBuilder;
import doctor4t.astronomical.client.render.world.VertexData;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.entity.FallenStarEntity;
import doctor4t.astronomical.common.init.ModParticles;
import doctor4t.astronomical.common.init.ModStatusEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.setup.LodestoneRenderLayers;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;
import team.lodestar.lodestone.systems.rendering.particle.Easing;
import team.lodestar.lodestone.systems.rendering.particle.LodestoneWorldParticleTextureSheet;
import team.lodestar.lodestone.systems.rendering.particle.WorldParticleBuilder;
import team.lodestar.lodestone.systems.rendering.particle.data.ColorParticleData;
import team.lodestar.lodestone.systems.rendering.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.rendering.particle.data.SpinParticleData;

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
			WorldParticleBuilder.create(ModParticles.FALLEN_STAR)
					.setScaleData(GenericParticleData.create((.8f + entity.getWorld().random.nextFloat() / 3f)).build())
					.setColorData(ColorParticleData.create(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE).build())
					.setTransparencyData(GenericParticleData.create(0, 0.3f, 0).build())
					.enableNoClip()
					.setLifetime(20)
					.setSpinData(SpinParticleData.create((float) (entity.getWorld().random.nextGaussian() / 100f)).setSpinOffset(entity.getWorld().random.nextFloat() * 360f).build())
					.spawn(entity.getWorld(), entity.getX(), entity.getY() + entity.getHeight() / 2f, entity.getZ());

			WorldParticleBuilder.create(ModParticles.STAR_IMPACT_FLARE)
					.setScaleData(GenericParticleData.create((6f + entity.getWorld().random.nextFloat() * 5f)).build())
					.setColorData(ColorParticleData.create(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE).build())
					.setTransparencyData(GenericParticleData.create(0, 0.2f, 0).build())
					.enableNoClip()
					.setLifetime(20)
					.setSpinData(SpinParticleData.create((float) (entity.getWorld().random.nextGaussian() / 100f)).setSpinOffset(entity.getWorld().random.nextFloat() * 360f).build())
					.spawn(entity.getWorld(), entity.getX(), entity.getY() + entity.getHeight() / 2f, entity.getZ());

			WorldParticleBuilder.create(ModParticles.FALLEN_STAR)
					.setScaleData(GenericParticleData.create((.2f + entity.getWorld().random.nextFloat() / 10f)).build())
					.setColorData(ColorParticleData.create(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE).build())
					.setTransparencyData(GenericParticleData.create(0, .5f, 0).build())
					.enableNoClip()
					.setLifetime(20 + entity.getWorld().random.nextInt(20))
					.setSpinData(SpinParticleData.create((float) (entity.getWorld().random.nextGaussian() / 50f)).setSpinOffset(entity.getWorld().random.nextFloat() * 360f).build())
					.setRandomOffset(entity.getWorld().random.nextFloat() * Math.signum(entity.getWorld().random.nextGaussian()) * 10f)
					.spawn(entity.getWorld(), entity.getX(), entity.getY() + entity.getHeight() / 2f, entity.getZ());
		}

		if (MinecraftClient.getInstance().player.hasStatusEffect(ModStatusEffects.STARGAZING) && MinecraftClient.getInstance().player.getStatusEffect(ModStatusEffects.STARGAZING).getAmplifier() > 0) {
			MinecraftClient client = MinecraftClient.getInstance();
			Vec3d playerPos = client.player != null ? client.player.getCameraPosVec(tickDelta) : Vec3d.ZERO;
			Vec3d diff = entity.getPos().subtract(playerPos);
			float easein = MathHelper.lerp(Easing.SINE_OUT.ease(MathHelper.clamp(entity.age / 100f, 0, 1), 0, 1, 1), 0f, 1f);
			Vec3d dirVec = new Vec3d(0, easein * 100f, 0);
			Color color = Astronomical.STAR_PURPLE.darker();
			VertexData d = AstraWorldVFXBuilder.createFadeoutVertexData(diff, dirVec, 1f, 1f, color, 0, -(entity.getWorld().getTime() + tickDelta % 190) / 190f);

			((AstraWorldVFXBuilder) builder.setOffset((float) ((float) -entity.getX() + playerPos.getX()), (float) ((float) -entity.getY() + playerPos.getY()), (float) ((float) -entity.getZ() + playerPos.getZ())).setAlpha(easein * (1 - MathHelper.clamp(25 / (float) diff.length(), 0, 1)))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(AstraSkyRenderer.SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);

			diff = entity.getPos().subtract(playerPos).add(-0.08, -0.08, -0.08);

			d = AstraWorldVFXBuilder.createFadeoutVertexData(diff, dirVec, 1f, 1f, color, 0, (-(entity.getWorld().getTime() + tickDelta % 190) / 190f + 0.1f) * 1.2f);

			((AstraWorldVFXBuilder) builder.setOffset((float) ((float) -entity.getX() + playerPos.getX()), (float) ((float) -entity.getY() + playerPos.getY()), (float) ((float) -entity.getZ() + playerPos.getZ())).setAlpha(easein * (1 - MathHelper.clamp(25 / (float) diff.length(), 0, 1)))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(AstraSkyRenderer.SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);

			diff = entity.getPos().subtract(playerPos).add(0.08, 0.08, 0.08);

			d = AstraWorldVFXBuilder.createFadeoutVertexData(diff, dirVec, 1f, 1f, color, 0, (-(entity.getWorld().getTime() + tickDelta % 190) / 190f + 0.6f) * 0.9f);

			((AstraWorldVFXBuilder) builder.setOffset((float) ((float) -entity.getX() + playerPos.getX()), (float) ((float) -entity.getY() + playerPos.getY()), (float) ((float) -entity.getZ() + playerPos.getZ())).setAlpha(easein * (1 - MathHelper.clamp(25 / (float) diff.length(), 0, 1)))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(AstraSkyRenderer.SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);
		}

		if (!entity.getStack().isEmpty()) {
			float time = ((float) (MinecraftClient.getInstance().world.getTime() % 2400000L) + MinecraftClient.getInstance().getTickDelta());
			ItemStack visualStack = entity.getStack();
			visualStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("size", 1);
			matrices.push();
			matrices.translate(0, entity.getHeight() / 2f, 0);
			matrices.scale(.18f, .18f, .18f);
			matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(time));
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
