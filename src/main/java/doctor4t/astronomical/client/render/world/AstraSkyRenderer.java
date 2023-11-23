package doctor4t.astronomical.client.render.world;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.sammy.lodestone.helpers.RenderHelper;
import com.sammy.lodestone.setup.LodestoneShaders;
import doctor4t.astronomical.cca.AstraCardinalComponents;
import doctor4t.astronomical.common.structure.CelestialObject;
import doctor4t.astronomical.common.structure.Star;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.*;
import org.apache.logging.log4j.util.TriConsumer;

import java.awt.*;

public class AstraSkyRenderer {
	private static final Color yeah = new Color(255, 107, 160, 200);
	private static final Vec3d UP = new Vec3d(0, 1, 0);
	private static VertexBuffer stars = null;
	private static boolean shouldTankPerformanceForAFewFrames = false;
	public static void renderSky(MatrixStack matrices, VertexConsumerProvider provider, Matrix4f projectionMatrix, Frustum frustum, float tickDelta, Runnable runnable, ClientWorld world, MinecraftClient client) {
		if(stars == null || shouldTankPerformanceForAFewFrames) {
			renderToStarBuffer(matrices, provider, projectionMatrix, frustum, tickDelta, runnable, world, client);
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

		RenderSystem.disableBlend();
		RenderSystem.disableTexture();
		matrices.pop();
	}
	public void redrawStars() {
		shouldTankPerformanceForAFewFrames = true;
	}
	public static void renderToStarBuffer(MatrixStack matrices, VertexConsumerProvider provider, Matrix4f projectionMatrix, Frustum frustum, float tickDelta, Runnable runnable, ClientWorld world, MinecraftClient client) {
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

		for(CelestialObject c : world.getComponent(AstraCardinalComponents.SKY).getCelestialObjects()) {
			Vec3d vector = c.getDirectionVector();

			VertexData p = createVertexData(vector, up, c.getSize(), 100, yeah);
//			if(shouldRender(((AstraFrustum)frustum), p, rotation))
			apply((v, col, u) -> bufferBuilder.vertex(matrix4f, v.getX(), v.getY(), v.getZ()).color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()).uv(u.x, u.y).light(RenderHelper.FULL_BRIGHT).next(), p);
		}
		stars.bind();
		stars.upload(bufferBuilder.end());
		VertexBuffer.unbind();
	}
	private static void apply(TriConsumer<Vec3f, Color, Vec2f> gungy, VertexData data) {
		for(int i = 0; i < 4; i++) {
			gungy.accept(data.vertices()[i], data.color(), data.uv()[i]);
		}
	}

	private static boolean shouldRender(AstraFrustum a, VertexData vecs, Quaternion q) {
		for(Vec3f vec : vecs.vertices()) {
			Vec3f newVec = new Vec3f(vec.getX(), vec.getY(), vec.getZ());
            newVec.rotate(q);
			if(a.astra$isVisible(newVec.getX(), newVec.getY(), newVec.getZ())) {
				return true;
			}
		}
		return false;
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

	public static Vec3d rotateViaQuat(Vec3d rot, Quaternion quat) {
		float x = (float) rot.x;
		float y = (float) rot.y;
		float z = (float) rot.z;

		float ux = quat.getX();
		float uy = quat.getY();
		float uz = quat.getZ();

		float scalar = quat.getW();

		float cx = -y*uz+(z*uy);
		float cy = -z*ux+(x*uz);
		float cz = -x*uy+(y*ux);

		double s1 = 2.0f * (ux*x + uy*y + uz*z);
		double s2 = scalar*scalar - (ux*ux + uy*uy + uz*uz);
		double s3 = 2.0f * scalar;

		double vpx = s1 * ux + s2 * x + s3 * cx;
		double vpy = s1 * uy + s2 * y + s3 * cy;
		double vpz = s1 * uz + s2 * z + s3 * cz;

		return new Vec3d(vpx, vpy, vpz);
	}
}
