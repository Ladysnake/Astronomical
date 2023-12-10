package doctor4t.astronomical.client.render.entity;

import com.sammy.lodestone.handlers.RenderHandler;
import com.sammy.lodestone.setup.LodestoneRenderLayers;
import com.sammy.lodestone.systems.rendering.VFXBuilders;
import com.sammy.lodestone.systems.rendering.particle.Easing;
import doctor4t.astronomical.client.render.world.AstraSkyRenderer;
import doctor4t.astronomical.client.render.world.AstraWorldVFXBuilder;
import doctor4t.astronomical.client.render.world.VertexData;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.entity.FallenStarEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class FallenStarEntityRenderer extends EntityRenderer<FallenStarEntity> {
	public FallenStarEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
	}

	@Override
	public void render(FallenStarEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		if (MinecraftClient.getInstance().player.hasStatusEffect(Astronomical.STARGAZING_EFFECT) && MinecraftClient.getInstance().player.getStatusEffect(Astronomical.STARGAZING_EFFECT).getAmplifier() > 0) {
			VFXBuilders.WorldVFXBuilder builder = new AstraWorldVFXBuilder().setPosColorTexLightmapDefaultFormat();
			MinecraftClient client = MinecraftClient.getInstance();
			Vec3d playerPos = client.player != null ? client.player.getCameraPosVec(tickDelta) : Vec3d.ZERO;
			Vec3d diff = entity.getPos().subtract(playerPos);
			float easein = MathHelper.lerp(Easing.SINE_OUT.ease(MathHelper.clamp(entity.age / 100f, 0, 1), 0, 1, 1), 0f, 1f);
			Vec3d dirVec = new Vec3d(0, easein*100f, 0);
			Color color = Astronomical.STAR_PURPLE.darker();
			VertexData d = AstraWorldVFXBuilder.createFadeoutVertexData(diff, dirVec, 1f, 1f, color, 0, -(entity.world.getTime() + tickDelta % 190) / 190f);

			((AstraWorldVFXBuilder) builder.setOffset((float) ((float) -entity.getX() + playerPos.getX()), (float) ((float) -entity.getY() + playerPos.getY()), (float) ((float) -entity.getZ() + playerPos.getZ())).setAlpha(easein*(1 - MathHelper.clamp(25 / (float) diff.length(), 0, 1)))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(AstraSkyRenderer.SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);

			diff = entity.getPos().subtract(playerPos).add(-0.08, -0.08, -0.08);

			d = AstraWorldVFXBuilder.createFadeoutVertexData(diff, dirVec, 1f, 1f, color, 0, (-(entity.world.getTime() + tickDelta % 190) / 190f + 0.1f) * 1.2f);

			((AstraWorldVFXBuilder) builder.setOffset((float) ((float) -entity.getX() + playerPos.getX()), (float) ((float) -entity.getY() + playerPos.getY()), (float) ((float) -entity.getZ() + playerPos.getZ())).setAlpha(easein*(1 - MathHelper.clamp(25 / (float) diff.length(), 0, 1)))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(AstraSkyRenderer.SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);

			diff = entity.getPos().subtract(playerPos).add(0.08, 0.08, 0.08);

			d = AstraWorldVFXBuilder.createFadeoutVertexData(diff, dirVec, 1f, 1f, color, 0, (-(entity.world.getTime() + tickDelta % 190) / 190f + 0.6f) * 0.9f);

			((AstraWorldVFXBuilder) builder.setOffset((float) ((float) -entity.getX() + playerPos.getX()), (float) ((float) -entity.getY() + playerPos.getY()), (float) ((float) -entity.getZ() + playerPos.getZ())).setAlpha(easein*(1 - MathHelper.clamp(25 / (float) diff.length(), 0, 1)))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(AstraSkyRenderer.SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);
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
