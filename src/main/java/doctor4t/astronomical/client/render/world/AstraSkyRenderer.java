package doctor4t.astronomical.client.render.world;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import com.sammy.lodestone.helpers.RenderHelper;
import com.sammy.lodestone.setup.LodestoneShaders;
import com.sammy.lodestone.systems.rendering.particle.Easing;
import doctor4t.astronomical.cca.AstraCardinalComponents;
import doctor4t.astronomical.common.structure.CelestialObject;
import doctor4t.astronomical.common.structure.InteractableStar;
import doctor4t.astronomical.common.structure.Star;
import doctor4t.astronomical.common.util.ColourRamp;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.apache.logging.log4j.util.TriConsumer;

import java.awt.Color;
import java.util.List;

public class AstraSkyRenderer {
	private static final ColourRamp starColour = new ColourRamp(new Color(205, 20, 200), new Color(205, 255, 255), Easing.SINE_IN);
	private static final Vec3d UP = new Vec3d(0, 1, 0);
	private static VertexBuffer stars = null;
	private static VertexBuffer interactableStars = null;
	private static boolean shouldTankPerformanceForAFewFrames = false;
	public static void renderSky(MatrixStack matrices, VertexConsumerProvider provider, Matrix4f projectionMatrix, float tickDelta, Runnable runnable, ClientWorld world, MinecraftClient client) {
		if(stars == null || shouldTankPerformanceForAFewFrames) {
			List<CelestialObject> c = world.getComponent(AstraCardinalComponents.SKY).getCelestialObjects();
			renderToStarBuffer(matrices, provider, projectionMatrix, tickDelta, runnable, world, client, c);
			renderToInteractableStarBuffer(matrices, provider, projectionMatrix, tickDelta, runnable, world, client, c);
			shouldTankPerformanceForAFewFrames = false;
		}
		Quaternion rotation = Vec3f.POSITIVE_Z.getDegreesQuaternion(world.getSkyAngle(1) * 360.0F);
//		Vec3d up = rotateViaQuat(UP, invert(rotation));
		rotation.normalize();

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

		if(interactableStars != null) {
			RenderSystem.setShaderTexture(0, InteractableStar.INTERACTABLE_TEX);
			interactableStars.bind();
			interactableStars.setShader(matrices.peek().getModel(), projectionMatrix, LodestoneShaders.ADDITIVE_TEXTURE.getInstance().get());
			VertexBuffer.unbind();
		}

		RenderSystem.disableBlend();
		RenderSystem.disableTexture();
		matrices.pop();
	}
	public static void redrawStars() {
		shouldTankPerformanceForAFewFrames = true;
	}
	public static void renderToStarBuffer(MatrixStack matrices, VertexConsumerProvider provider, Matrix4f projectionMatrix, float tickDelta, Runnable runnable, ClientWorld world, MinecraftClient client, List<CelestialObject> objects) {
		Vec3d up = UP;

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

			VertexData p = createVertexData(vector, up, c.getSize()+0.5f*c.getHeat(), 100, starColour.ease(c.getHeat()));
//			if(shouldRender(((AstraFrustum)frustum), p, rotation))
			apply((v, col, u) -> bufferBuilder.vertex(matrix4f, v.getX(), v.getY(), v.getZ()).color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()).uv(u.x, u.y).light(RenderHelper.FULL_BRIGHT).next(), p);
		}
		stars.bind();
		stars.upload(bufferBuilder.end());
		VertexBuffer.unbind();
	}
	public static void renderToInteractableStarBuffer(MatrixStack matrices, VertexConsumerProvider provider, Matrix4f projectionMatrix, float tickDelta, Runnable runnable, ClientWorld world, MinecraftClient client, List<CelestialObject> objects) {
		Vec3d up = UP;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		RenderSystem.setShader(LodestoneShaders.ADDITIVE_TEXTURE.getInstance());
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
		Matrix4f matrix4f = matrices.peek().getModel();

		if(interactableStars != null) {
			interactableStars.close();
		}
		interactableStars  = new VertexBuffer();

		for(CelestialObject c : objects.stream().filter(CelestialObject::canInteract).toList()) {
			Vec3d vector = c.getDirectionVector();

			VertexData p = createVertexData(vector, up, c.getSize()+0.4f*c.getHeat(), 100, starColour.ease(c.getHeat()));
//			if(shouldRender(((AstraFrustum)frustum), p, rotation))
			apply((v, col, u) -> bufferBuilder.vertex(matrix4f, v.getX(), v.getY(), v.getZ()).color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()).uv(u.x, u.y).light(RenderHelper.FULL_BRIGHT).next(), p);
		}
		interactableStars.bind();
		interactableStars.upload(bufferBuilder.end());
		VertexBuffer.unbind();
	}
	private static void apply(TriConsumer<Vec3f, Color, Vec2f> gungy, VertexData data) {
		for(int i = 0; i < 4; i++) {
			gungy.accept(data.vertices()[i], data.color(), data.uv()[i]);
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
		t1x *= size; t1y *= size; t1z *= size;

		float t2x = -y*t1z+(z*t1y);
		float t2y = -z*t1x+(x*t1z);
		float t2z = -x*t1y+(y*t1x);

		float t2d2 = (float) Math.sqrt(distanceSquared(t2x, t2y, t2z));

		t2x /= t2d2; t2y /= t2d2; t2z /= t2d2;
		t2x *= size; t2y *= size; t2z *= size;

		x *= distance;
		y *= distance;
		z *= distance;

		return new VertexData(new Vec3f[]{new Vec3f(x + t1x + t2x, y + t1y + t2y, z + t1z + t2z), new Vec3f(x - t1x + t2x, y - t1y + t2y, z - t1z + t2z),  new Vec3f(x - t1x - t2x, y - t1y - t2y, z - t1z - t2z), new Vec3f(x + t1x - t2x, y + t1y - t2y, z + t1z - t2z)}, c, new Vec2f[]{new Vec2f(0, 1), new Vec2f(1, 1), new Vec2f(1, 0), new Vec2f(0, 0)});
	}

	public static Quaternion invert(Quaternion in) {
		float invNorm = 1.0f / Math.fma(in.getX(), in.getX(), Math.fma(in.getY(), in.getY(), Math.fma(in.getZ(), in.getZ(), in.getW() * in.getW())));
		return new Quaternion(-in.getX() * invNorm, -in.getY() * invNorm, -in.getZ() * invNorm, in.getW() * invNorm);
	}

	private static float distanceSquared(float x, float y, float z) {
		return x*x + y*y + z*z;
	}


}
