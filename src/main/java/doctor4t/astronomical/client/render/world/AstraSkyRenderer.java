package doctor4t.astronomical.client.render.world;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.sammy.lodestone.handlers.RenderHandler;
import com.sammy.lodestone.helpers.RenderHelper;
import com.sammy.lodestone.setup.LodestoneRenderLayers;
import com.sammy.lodestone.setup.LodestoneShaders;
import com.sammy.lodestone.systems.rendering.VFXBuilders;
import doctor4t.astronomical.cca.AstraCardinalComponents;
import doctor4t.astronomical.cca.world.AstraStarfallComponent;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.structure.CelestialObject;
import doctor4t.astronomical.common.structure.InteractableStar;
import doctor4t.astronomical.common.structure.Star;
import doctor4t.astronomical.common.structure.Starfall;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import org.apache.logging.log4j.util.TriConsumer;

import java.awt.Color;
import java.util.List;

public class AstraSkyRenderer {
	public static final Identifier SHIMMER = Astronomical.id("textures/vfx/shimmer.png");
	private static final Vec3d UP = new Vec3d(0, 1, 0);
	private static VertexBuffer stars = null;
	private static boolean shouldTankPerformanceForAFewFrames = false;
	public static void renderSky(MatrixStack matrices, VertexConsumerProvider provider, Matrix4f projectionMatrix, float tickDelta, ClientWorld world, MinecraftClient client) {
		//matrices = new MatrixStack();
		if(stars == null || shouldTankPerformanceForAFewFrames) {
			List<CelestialObject> c = world.getComponent(AstraCardinalComponents.SKY).getCelestialObjects();
			MatrixStack temp = new MatrixStack();
			renderToStarBuffer(temp, provider, projectionMatrix, tickDelta, world, client, c);
			shouldTankPerformanceForAFewFrames = false;
		}
		Quaternion rotation = Vec3f.POSITIVE_Z.getDegreesQuaternion(world.getSkyAngle(tickDelta) * 360.0F);
//		Vec3d up = rotateViaQuat(UP, invert(rotation));
		//rotation.normalize();

		matrices.push();
		matrices.multiply(rotation);

		RenderSystem.enableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(1, 1, 1, world.getStarBrightness(tickDelta));
		RenderSystem.setShaderTexture(0, Star.TEMPTEX);

		stars.bind();
		stars.setShader(matrices.peek().getModel(), projectionMatrix, LodestoneShaders.ADDITIVE_TEXTURE.getInstance().get());
		VertexBuffer.unbind();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		RenderSystem.setShader(LodestoneShaders.ADDITIVE_TEXTURE.getInstance());
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
		RenderSystem.setShaderTexture(0, InteractableStar.INTERACTABLE_TEX);
		Matrix4f matrix4f = matrices.peek().getModel();

		for(CelestialObject c : world.getComponent(AstraCardinalComponents.SKY).getCelestialObjects().stream().filter(CelestialObject::canInteract).toList()) {
			Vec3d vector = c.getDirectionVector();

			VertexData p = createVertexData(vector, UP, c.getSize()+0.4f*c.getHeat(), 95, Color.WHITE);
//			if(shouldRender(((AstraFrustum)frustum), p, rotation))
			apply((v, col, u) -> bufferBuilder.vertex(matrix4f, v.getX(), v.getY(), v.getZ()).color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()).uv(u.x, u.y).light(RenderHelper.FULL_BRIGHT).next(), p);
		}
		BufferRenderer.drawWithShader(bufferBuilder.end());

		RenderSystem.disableBlend();
		RenderSystem.disableTexture();
		matrices.pop();
		renderStarfalls(matrices, tickDelta, world, client);
	}
	public static void redrawStars() {
		shouldTankPerformanceForAFewFrames = true;
	}

	public static final Color STARFALL = new Color(230, 185, 255, 200);

	public static void renderStarfalls(MatrixStack matrices, float tickDelta, ClientWorld world, MinecraftClient client) {
		matrices.push();
		AstraStarfallComponent c = world.getComponent(AstraCardinalComponents.FALL);
		VFXBuilders.WorldVFXBuilder builder = new AstraWorldVFXBuilder().setPosColorTexLightmapDefaultFormat();
		Vec3d playerPos = client.player != null ? client.player.getCameraPosVec(tickDelta) : Vec3d.ZERO;
		for(Starfall s : c.getStarfalls()) {
				if (s.progress < 71) {
					Vec3d one = playerPos.add(s.startDirection.multiply(100f));
					Vec3d two = s.endPos;
					float delta = (s.progress + tickDelta) / 70f;
					float alpha = 0.6f*(1-delta)+delta;
					one = one.lerp(two, delta);
					VertexData d = createVertexData(one.subtract(playerPos), UP, 10, Color.WHITE);
					builder
						.setColor(d.color()[0])
						.setAlpha(alpha)
						.renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(InteractableStar.INTERACTABLE_TEX)), matrices, d.vertices(), 1);
				} else {
					Vec3d directionalVector = s.startDirection.multiply(40f + MathHelper.clamp(30f / (s.progress + tickDelta - 70), 0, 30));
					Vec3d pos = s.endPos;

					Vec3d diff = pos.subtract(playerPos);

					VertexData d = createFadeoutVertexData(diff, directionalVector, 2f, 1.4f, STARFALL, 0, -(world.getTime() + tickDelta % 190) / 190f);

					((AstraWorldVFXBuilder) builder.setAlpha(1 - MathHelper.clamp(8 / (float) diff.length(), 0, 1))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);

					diff = pos.subtract(playerPos).add(-0.08, -0.08, -0.08);

					d = createFadeoutVertexData(diff, directionalVector, 2f, 1.4f, STARFALL, 0, (-(world.getTime() + tickDelta % 190) / 190f + 0.1f)*1.2f);

					((AstraWorldVFXBuilder) builder.setAlpha(1 - MathHelper.clamp(8 / (float) diff.length(), 0, 1))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);

					diff = pos.subtract(playerPos).add(0.08, 0.08, 0.08);

					d = createFadeoutVertexData(diff, directionalVector, 2f, 1.4f, STARFALL, 0, (-(world.getTime() + tickDelta % 190) / 190f + 0.6f)*0.9f);

					((AstraWorldVFXBuilder) builder.setAlpha(1 - MathHelper.clamp(8 / (float) diff.length(), 0, 1))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);

				}
		}
		matrices.pop();
	}

	public static void renderToStarBuffer(MatrixStack matrices, VertexConsumerProvider provider, Matrix4f projectionMatrix, float tickDelta, ClientWorld world, MinecraftClient client, List<CelestialObject> objects) {
        Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		RenderSystem.setShader(LodestoneShaders.ADDITIVE_TEXTURE.getInstance());
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
		Matrix4f matrix4f = matrices.peek().getModel();

		if(stars != null) {
			stars.close();
		}
		stars = new VertexBuffer();

		for(CelestialObject c : objects.stream().filter(p -> !p.canInteract()).toList()) {
			Vec3d vector = c.getDirectionVector();

			VertexData p = createVertexData(vector, UP, c.getSize()+0.5f*c.getHeat(), 100, new Color(Astronomical.getRandomStarTemperature(c.getHeat())));
//			if(shouldRender(((AstraFrustum)frustum), p, rotation))
			apply((v, col, u) -> bufferBuilder.vertex(matrix4f, v.getX(), v.getY(), v.getZ()).color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()).uv(u.x, u.y).light(RenderHelper.FULL_BRIGHT).next(), p);
		}
		stars.bind();
		stars.upload(bufferBuilder.end());
		VertexBuffer.unbind();
	}
	private static void apply(TriConsumer<Vec3f, Color, Vec2f> gungy, VertexData data) {
		for(int i = 0; i < 4; i++) {
			gungy.accept(data.vertices()[i], data.color().length > 3 ? data.color()[i] : data.color()[0], data.uv()[i]);
		}
	}

	private static VertexData createVertexData(Vec3d dir, Vec3d up, float size, float distance, Color c) {
		float x = (float) dir.x;
		float y = (float) dir.y;
		float z = (float) dir.z;

		float ux = (float) up.x;
		float uy = (float) up.y;
		float uz = (float) up.z;

		float t1x = -y*uz+(z*uy);
		float t1y = -z*ux+(x*uz);
		float t1z = -x*uy+(y*ux);

		float t1d2 = (float) Math.sqrt(distanceSquared(t1x, t1y, t1z));

		t1x /= t1d2; t1y /= t1d2; t1z /= t1d2;

		float t2x = -y*t1z+(z*t1y);
		float t2y = -z*t1x+(x*t1z);
		float t2z = -x*t1y+(y*t1x);

		float t2d2 = (float) Math.sqrt(distanceSquared(t2x, t2y, t2z));

		t2x /= t2d2; t2y /= t2d2; t2z /= t2d2;
		t1x *= size; t1y *= size; t1z *= size;
		t2x *= size; t2y *= size; t2z *= size;

		x *= distance;
		y *= distance;
		z *= distance;

		return new VertexData(new Vec3f[]{new Vec3f(x + t1x + t2x, y + t1y + t2y, z + t1z + t2z), new Vec3f(x - t1x + t2x, y - t1y + t2y, z - t1z + t2z),  new Vec3f(x - t1x - t2x, y - t1y - t2y, z - t1z - t2z), new Vec3f(x + t1x - t2x, y + t1y - t2y, z + t1z - t2z)}, new Color[]{c}, new Vec2f[]{new Vec2f(0, 1), new Vec2f(1, 1), new Vec2f(1, 0), new Vec2f(0, 0)});
	}
	private static VertexData createFadeoutVertexData(Vec3d pos, Vec3d up, float beginSize, float endSize, Color c, int endAlpha, float vOffset) {
		Vec3d b = pos.normalize();
		float x = (float) b.x;
		float y = (float) b.y;
		float z = (float) b.z;

		Vec3d dir = up.normalize();
		float ux = (float) -dir.x;
		float uy = (float) -dir.y;
		float uz = (float) -dir.z;

		float t1x = -y*uz+(z*uy);
		float t1y = -z*ux+(x*uz);
		float t1z = -x*uy+(y*ux);

		float t1d2 = (float) Math.sqrt(distanceSquared(t1x, t1y, t1z));

		t1x /= t1d2; t1y /= t1d2; t1z /= t1d2;

		x = (float) pos.x;
		y = (float) pos.y;
		z = (float) pos.z;

		ux = (float) up.x;
		uy = (float) up.y;
		uz = (float) up.z;

		return new VertexData(new Vec3f[]{new Vec3f(x + t1x*endSize + ux, y + t1y*endSize + uy, z + t1z*endSize + uz), new Vec3f(x - t1x*endSize + ux, y - t1y*endSize + uy, z - t1z*endSize + uz),  new Vec3f(x - t1x*beginSize, y - t1y*beginSize, z - t1z*beginSize), new Vec3f(x + t1x*beginSize, y + t1y*beginSize, z + t1z*beginSize)}, new Color[]{new Color(c.getRed(), c.getGreen(), c.getBlue(), endAlpha), new Color(c.getRed(), c.getGreen(), c.getBlue(), endAlpha), c, c}, new Vec2f[]{new Vec2f(0, 6+vOffset), new Vec2f(1, 6+vOffset), new Vec2f(1, 0+vOffset), new Vec2f(0, 0+vOffset)});
	}

	private static VertexData createVertexData(Vec3d pos, Vec3d up, float size, Color c) {
		Vec3d b = pos.normalize();
		float x = (float) b.x;
		float y = (float) b.y;
		float z = (float) b.z;

		float ux = (float) up.x;
		float uy = (float) up.y;
		float uz = (float) up.z;

		float t1x = -y*uz+(z*uy);
		float t1y = -z*ux+(x*uz);
		float t1z = -x*uy+(y*ux);

		float t1d2 = (float) Math.sqrt(distanceSquared(t1x, t1y, t1z));

		t1x /= t1d2; t1y /= t1d2; t1z /= t1d2;

		float t2x = -y*t1z+(z*t1y);
		float t2y = -z*t1x+(x*t1z);
		float t2z = -x*t1y+(y*t1x);

		float t2d2 = (float) Math.sqrt(distanceSquared(t2x, t2y, t2z));

		t2x /= t2d2; t2y /= t2d2; t2z /= t2d2;
		t1x *= size; t1y *= size; t1z *= size;
		t2x *= size; t2y *= size; t2z *= size;

		x = (float) pos.x;
		y = (float) pos.y;
		z = (float) pos.z;

		return new VertexData(new Vec3f[]{new Vec3f(x + t1x + t2x, y + t1y + t2y, z + t1z + t2z), new Vec3f(x - t1x + t2x, y - t1y + t2y, z - t1z + t2z),  new Vec3f(x - t1x - t2x, y - t1y - t2y, z - t1z - t2z), new Vec3f(x + t1x - t2x, y + t1y - t2y, z + t1z - t2z)}, new Color[]{c}, new Vec2f[]{new Vec2f(0, 1), new Vec2f(1, 1), new Vec2f(1, 0), new Vec2f(0, 0)});
	}

	public static Quaternion invert(Quaternion in) {
		float invNorm = 1.0f / Math.fma(in.getX(), in.getX(), Math.fma(in.getY(), in.getY(), Math.fma(in.getZ(), in.getZ(), in.getW() * in.getW())));
		return new Quaternion(-in.getX() * invNorm, -in.getY() * invNorm, -in.getZ() * invNorm, in.getW() * invNorm);
	}

	private static float distanceSquared(float x, float y, float z) {
		return x*x + y*y + z*z;
	}


}
