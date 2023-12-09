package doctor4t.astronomical.client.render.world;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.sammy.lodestone.handlers.RenderHandler;
import com.sammy.lodestone.handlers.ScreenshakeHandler;
import com.sammy.lodestone.helpers.RenderHelper;
import com.sammy.lodestone.setup.LodestoneParticles;
import com.sammy.lodestone.setup.LodestoneRenderLayers;
import com.sammy.lodestone.setup.LodestoneScreenParticles;
import com.sammy.lodestone.setup.LodestoneShaders;
import com.sammy.lodestone.systems.rendering.VFXBuilders;
import com.sammy.lodestone.systems.rendering.particle.Easing;
import com.sammy.lodestone.systems.rendering.particle.ParticleBuilders;
import com.sammy.lodestone.systems.rendering.particle.screen.base.ScreenParticle;
import com.sammy.lodestone.systems.screenshake.ScreenshakeInstance;
import doctor4t.astronomical.cca.AstraCardinalComponents;
import doctor4t.astronomical.cca.world.AstraSkyComponent;
import doctor4t.astronomical.cca.world.AstraStarfallComponent;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.init.ModParticles;
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
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class AstraSkyRenderer {
	public static final Identifier SHIMMER = Astronomical.id("textures/vfx/shimmer.png");
	public static final Color STARFALL = new Color(90, 214, 255, 255);
	private static final Vec3d UP = new Vec3d(0, 1, 0);
	private static VertexBuffer stars = null;
	private static boolean shouldTankPerformanceForAFewFrames = false;

	public static void renderSky(MatrixStack matrices, VertexConsumerProvider provider, Matrix4f projectionMatrix, float tickDelta, ClientWorld world, MinecraftClient client) {
		//matrices = new MatrixStack();
		if (stars == null || shouldTankPerformanceForAFewFrames) {
			List<CelestialObject> c = world.getComponent(AstraCardinalComponents.SKY).getCelestialObjects();
			MatrixStack temp = new MatrixStack();
			renderToStarBuffer(temp, provider, projectionMatrix, tickDelta, world, client, c);
			shouldTankPerformanceForAFewFrames = false;
		}
		float time = ((float) (MinecraftClient.getInstance().world.getTime() % 2400000L) + tickDelta);
		Quaternion rotation = Vec3f.POSITIVE_Z.getDegreesQuaternion(world.getSkyAngle(tickDelta) * 360.0F);
//		Vec3d up = rotateViaQuat(UP, invert(rotation));
		//rotation.normalize();

		matrices.push();
		matrices.multiply(rotation);

		RenderSystem.enableTexture();
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		RenderSystem.setShaderColor(1, 1, 1, world.getStarBrightness(tickDelta));
		RenderSystem.setShaderTexture(0, Star.TEMPTEX);

		stars.bind();
		stars.setShader(matrices.peek().getModel(), projectionMatrix, LodestoneShaders.ADDITIVE_TEXTURE.getInstance().get());
		VertexBuffer.unbind();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		Matrix4f matrix4f = matrices.peek().getModel();

		// interactable stars
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
		RenderSystem.setShader(LodestoneShaders.ADDITIVE_TEXTURE.getInstance());
		RenderSystem.setShaderTexture(0, InteractableStar.INTERACTABLE_TEX);
		int ran = 0;
		float scale = .2f;
		AstraSkyComponent com = world.getComponent(AstraCardinalComponents.SKY);

		List<CelestialObject> list = com.getCelestialObjects().stream().filter(celestialObject -> celestialObject instanceof InteractableStar interactableStar && interactableStar.supernovaTicks <= InteractableStar.HOLD_COLLAPSE_TICKS).toList();
		for (CelestialObject c : list) {
			Vec3d vector = c.getDirectionVector();
			float rot1 = (time + ran + tickDelta) / 50f;
			float rot2 = (time + ran + tickDelta + 10) / 100f;
			float rot3 = -(time + ran + tickDelta + 20) / 50f;
			float rot4 = -(time + ran + tickDelta + 20) / 100f;

			int alpha = (int) (c.getAlpha() * 255);

			VertexData p = createVertexData(vector, UP, (scale + 0.1f * ((1 + MathHelper.sin(rot1)) / 2f)) * .5f * c.getSize(), 95, rot1, Color.WHITE);
			apply((v, col, u) -> bufferBuilder.vertex(matrix4f, v.getX(), v.getY(), v.getZ()).color(col.getRed(), col.getGreen(), col.getBlue(), alpha).uv(u.x, u.y).light(RenderHelper.FULL_BRIGHT).next(), p);
			VertexData p2 = createVertexData(vector, UP, (scale + 0.2f * ((1 + MathHelper.sin(rot2)) / 2f)) * .5f * c.getSize(), 95, rot2, Color.WHITE);
			apply((v, col, u) -> bufferBuilder.vertex(matrix4f, v.getX(), v.getY(), v.getZ()).color(col.getRed(), col.getGreen(), col.getBlue(), alpha).uv(u.x, u.y).light(RenderHelper.FULL_BRIGHT).next(), p2);
			VertexData p3 = createVertexData(vector, UP, (scale + 0.3f * ((1 + MathHelper.sin(rot3)) / 2f)) * .5f * c.getSize(), 95, rot3, Color.WHITE);
			apply((v, col, u) -> bufferBuilder.vertex(matrix4f, v.getX(), v.getY(), v.getZ()).color(col.getRed(), col.getGreen(), col.getBlue(), alpha).uv(u.x, u.y).light(RenderHelper.FULL_BRIGHT).next(), p3);
			VertexData p4 = createVertexData(vector, UP, (scale + 0.4f * ((1 + MathHelper.sin(rot4)) / 2f)) * .5f * c.getSize(), 95, rot4, Color.WHITE);
			apply((v, col, u) -> bufferBuilder.vertex(matrix4f, v.getX(), v.getY(), v.getZ()).color(col.getRed(), col.getGreen(), col.getBlue(), alpha).uv(u.x, u.y).light(RenderHelper.FULL_BRIGHT).next(), p4);

			ran += 200;
		}
		drawWithShader(bufferBuilder.end());

		// supernovae explosions
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
		RenderSystem.setShaderTexture(0, InteractableStar.SUPERNOVA_TEX);
		float yuh = 0;

		list = com.getCelestialObjects().stream().filter(celestialObject -> celestialObject instanceof InteractableStar interactableStar && interactableStar.supernovaTicks >= InteractableStar.HOLD_COLLAPSE_TICKS).toList();
		for (CelestialObject c : list) {
			Color supernovaColor = new Color(c.getColor());
			Vec3d vector = c.getDirectionVector();

			int alpha = (int) (c.getAlpha() * 255);

			for (int i = 0; i < 5; i++) {
				VertexData p = createVertexData(vector, UP, (scale + i * .02f) * c.getSize(), 95, yuh, Color.WHITE);
				apply((v, col, u) -> bufferBuilder.vertex(matrix4f, v.getX(), v.getY(), v.getZ()).color(supernovaColor.getRed(), supernovaColor.getGreen(), supernovaColor.getBlue(), alpha).uv(u.x, u.y).light(RenderHelper.FULL_BRIGHT).next(), p);

				yuh += c.getRandomOffset()/10f;
			}

			yuh += c.getRandomOffset()*10f;
		}
		drawWithShader(bufferBuilder.end());

		// supernovae explosions dust
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
		RenderSystem.setShaderTexture(0, InteractableStar.SUPERNOVA_DUST_TEX);
		yuh = 0;

		for (CelestialObject c : list) {
			Color supernovaColor = new Color(c.getColor());
			Vec3d vector = c.getDirectionVector();

			int alpha = (int) (c.getAlpha() * 255);

			for (int i = 0; i < 5; i++) {
				VertexData p = createVertexData(vector, UP, (scale * (i)) * c.getSize(), 95, yuh, Color.WHITE);
				apply((v, col, u) -> bufferBuilder.vertex(matrix4f, v.getX(), v.getY(), v.getZ()).color(supernovaColor.getRed(), supernovaColor.getGreen(), supernovaColor.getBlue(), alpha).uv(u.x, u.y).light(RenderHelper.FULL_BRIGHT).next(), p);
				yuh += c.getRandomOffset();
			}

			yuh += 200;
		}
		drawWithShader(bufferBuilder.end());

		RenderSystem.disableBlend();
		RenderSystem.disableTexture();
		matrices.pop();
		renderStarfalls(matrices, tickDelta, world, client);
		RenderSystem.defaultBlendFunc();
	}

	private static VertexBuffer upload(BufferBuilder.RenderedBuffer renderedBuffer) {
		RenderSystem.assertOnRenderThread();
		if (renderedBuffer.isEmpty()) {
			renderedBuffer.release();
			return null;
		} else {
			VertexBuffer vertexBuffer = getAndBindBuffer(renderedBuffer.getParameters().getVertexFormat());
			vertexBuffer.upload(renderedBuffer);
			return vertexBuffer;
		}
	}

	private static VertexBuffer getAndBindBuffer(VertexFormat format) {
		VertexBuffer vertexBuffer = format.getBuffer();
		vertexBuffer.bind();
		return vertexBuffer;
	}

	private static void drawWithShader(BufferBuilder.RenderedBuffer renderedBuffer) {
		VertexBuffer vertexBuffer = upload(renderedBuffer);
		if (vertexBuffer != null) {
			vertexBuffer.setShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), LodestoneShaders.ADDITIVE_TEXTURE.instance);
		}
	}

	public static void redrawStars() {
		shouldTankPerformanceForAFewFrames = true;
	}

	public static void renderStarfalls(MatrixStack matrices, float tickDelta, ClientWorld world, MinecraftClient client) {
		matrices.push();
		AstraStarfallComponent c = world.getComponent(AstraCardinalComponents.FALL);
		VFXBuilders.WorldVFXBuilder builder = new AstraWorldVFXBuilder().setPosColorTexLightmapDefaultFormat();
		Vec3d playerPos = client.player != null ? client.player.getCameraPosVec(tickDelta) : Vec3d.ZERO;

		for (Starfall s : c.getStarfalls()) {
			if (s.progress <= s.ticksUntilLanded * Starfall.ANIMATION_EXTENSION) {
				Vec3d one = playerPos.add(s.startDirection.multiply(1000f));
				Vec3d two = s.endPos;
				float delta = (s.progress + tickDelta) / s.ticksUntilLanded;
				one = one.lerp(two, s.progress <= s.ticksUntilLanded ? delta : 1);
				VertexData d = createVertexData(one.subtract(playerPos), UP, 10, Color.WHITE);

				// trail
				Vec3d directionalVector = s.progress <= s.ticksUntilLanded ? s.startDirection.multiply(MathHelper.lerp((float) s.progress / s.ticksUntilLanded, 10000, 100)) : s.startDirection.multiply(MathHelper.lerp((s.progress - s.ticksUntilLanded) / ((s.ticksUntilLanded * Starfall.ANIMATION_EXTENSION) - s.ticksUntilLanded), 100, 0));
				Vec3d pos = one;

				Vec3d diff = pos.subtract(playerPos);
				Color color = new Color(0xC065FF);

				d = createFadeoutVertexData(diff, directionalVector, 0.25f, 0f, color, 0, -(world.getTime() + tickDelta % 190) / 190f);

				((AstraWorldVFXBuilder) builder.setAlpha(1 - MathHelper.clamp(8 / (float) diff.length(), 0, 1))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);

				diff = pos.subtract(playerPos).add(-0.08, -0.08, -0.08);

				d = createFadeoutVertexData(diff, directionalVector, 0.25f, 0f, color, 0, (-(world.getTime() + tickDelta % 190) / 190f + 0.1f) * 1.2f);

				((AstraWorldVFXBuilder) builder.setAlpha(1 - MathHelper.clamp(8 / (float) diff.length(), 0, 1))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);

				diff = pos.subtract(playerPos).add(0.08, 0.08, 0.08);

				d = createFadeoutVertexData(diff, directionalVector, 0.25f, 0f, color, 0, (-(world.getTime() + tickDelta % 190) / 190f + 0.6f) * 0.9f);

				((AstraWorldVFXBuilder) builder.setAlpha(1 - MathHelper.clamp(8 / (float) diff.length(), 0, 1))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);


				if (s.progress == s.ticksUntilLanded && !s.hasPlayedExplosion()) {
					double h = MinecraftClient.getInstance().player.getPos().distanceTo(new Vec3d(one.getX(), one.getY(), one.getZ()));
					if (h < 300) {
						h /= 300;
						h *= (MinecraftClient.getInstance().cameraEntity.getRotationVecClient().dotProduct(new Vec3d(one.getX(), one.getY(), one.getZ()).subtract(MinecraftClient.getInstance().player.getPos()).normalize()) + 1) / 2;
						ParticleBuilders.ScreenParticleBuilder b2 = ParticleBuilders.create(LodestoneScreenParticles.SPARKLE).setAlpha((float) h, 0f).setScale(100000f).setColor(color, color).setLifetime(10).overrideRenderOrder(ScreenParticle.RenderOrder.AFTER_EVERYTHING);
						b2.repeat(0, 0, 1);

						ScreenshakeHandler.addScreenshake(new ScreenshakeInstance((int) (50 * h)).setIntensity(0f, 1f, 0f).setEasing(Easing.EXPO_OUT, Easing.SINE_OUT));
					}

					ParticleBuilders.create(ModParticles.STAR_IMPACT_FLARE)
						.setColor(color, color)
						.setScale(40f)
						.setMotion(0, 0, 0)
						.setAlpha(0f, 1f, 0f)
						.setAlphaEasing(Easing.CIRC_OUT)
						.enableNoClip()
						.setLifetime(10)
						.spawn(world, s.endPos.getX(), s.endPos.getY(), s.endPos.getZ());

					for (int i = 0; i < 20; ++i) {
						float size = world.random.nextFloat() * 20;
						Vec3f randomMotion = new Vec3f((float) (world.random.nextGaussian() / 8), 0, (float) (world.random.nextGaussian() / 8));
						float randomSpin = (float) (world.random.nextGaussian() * .01f);

						ParticleBuilders.create(ModParticles.STAR_IMPACT_EXPLOSION)
							.setColor(color, color)
							.setScale(0f, size)
							.setScaleCoefficient(20f)
							.setScaleEasing(Easing.EXPO_OUT)
							.setForcedMotion(randomMotion, Vec3f.ZERO)
							.setAlpha(0.1f, 0f)
							.setAlphaEasing(Easing.SINE_OUT)
							.setSpin(randomSpin, randomSpin / 100f)
							.setSpinEasing(Easing.SINE_IN)
							.enableNoClip()
							.setLifetime(50 + (i))
							.spawn(world, s.endPos.getX(), s.endPos.getY(), s.endPos.getZ());

						// bloom
						ParticleBuilders.create(ModParticles.STAR_IMPACT_EXPLOSION)
							.setScale(0f, size * 2f)
							.setScaleCoefficient(20f)
							.setScaleEasing(Easing.EXPO_OUT)
							.setForcedMotion(randomMotion, Vec3f.ZERO)
							.setAlpha(0.005f, 0f)
							.setAlphaEasing(Easing.SINE_OUT)
							.setSpin(randomSpin, randomSpin / 100f)
							.setSpinEasing(Easing.SINE_IN)
							.enableNoClip()
							.setLifetime(50 * (i))
							.spawn(world, s.endPos.getX(), s.endPos.getY(), s.endPos.getZ());
					}

					for (int i = 0; i < 20; ++i) {
						float size = world.random.nextFloat() * 20;
						Vec3f randomMotion = new Vec3f((float) world.random.nextGaussian() / 2f, (float) world.random.nextGaussian() / 2f, (float) world.random.nextGaussian() / 2f);
						float randomSpin = (float) (world.random.nextGaussian() * .1f);

						ParticleBuilders.create(LodestoneParticles.TWINKLE_PARTICLE)
							.setColor(color, color)
							.setScale(size, 0f)
							.setScaleEasing(Easing.EXPO_OUT)
							.setForcedMotion(randomMotion, Vec3f.ZERO)
							.setAlpha(1f)
							.setAlphaEasing(Easing.SINE_OUT)
							.setSpin(randomSpin, randomSpin / 100f)
							.setSpinEasing(Easing.SINE_IN)
							.enableNoClip()
							.setLifetime(20 + world.random.nextInt(20))
							.spawn(world, s.endPos.getX(), s.endPos.getY(), s.endPos.getZ());

					}

					s.setPlayedExplosion(true);
				}

			}
//			else {
//				Vec3d directionalVector = s.startDirection.multiply(40f + MathHelper.clamp(30f / (s.progress + tickDelta - s.ticksUntilLanded), 0, 30));
//				Vec3d pos = s.endPos;
//
//				Vec3d diff = pos.subtract(playerPos);
//
//				VertexData d = createFadeoutVertexData(diff, directionalVector, 2f, 1.4f, STARFALL, 0, -(world.getTime() + tickDelta % 190) / 190f);
//
//				((AstraWorldVFXBuilder) builder.setAlpha(1 - MathHelper.clamp(8 / (float) diff.length(), 0, 1))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);
//
//				diff = pos.subtract(playerPos).add(-0.08, -0.08, -0.08);
//
//				d = createFadeoutVertexData(diff, directionalVector, 2f, 1.4f, STARFALL, 0, (-(world.getTime() + tickDelta % 190) / 190f + 0.1f) * 1.2f);
//
//				((AstraWorldVFXBuilder) builder.setAlpha(1 - MathHelper.clamp(8 / (float) diff.length(), 0, 1))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);
//
//				diff = pos.subtract(playerPos).add(0.08, 0.08, 0.08);
//
//				d = createFadeoutVertexData(diff, directionalVector, 2f, 1.4f, STARFALL, 0, (-(world.getTime() + tickDelta % 190) / 190f + 0.6f) * 0.9f);
//
//				((AstraWorldVFXBuilder) builder.setAlpha(1 - MathHelper.clamp(8 / (float) diff.length(), 0, 1))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);
//			}
		}
		matrices.pop();
	}

	public static void renderToStarBuffer(MatrixStack matrices, VertexConsumerProvider provider, Matrix4f projectionMatrix, float tickDelta, ClientWorld world, MinecraftClient client, List<CelestialObject> objects) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		RenderSystem.setShader(LodestoneShaders.ADDITIVE_TEXTURE.getInstance());
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
		Matrix4f matrix4f = matrices.peek().getModel();

		if (stars != null) {
			stars.close();
		}
		stars = new VertexBuffer();

		for (CelestialObject c : objects.stream().filter(p -> !p.canInteract()).toList()) {
			Vec3d vector = c.getDirectionVector();

			VertexData p = createVertexData(vector, UP, c.getSize() + 0.5f, 100, new Color(Astronomical.getRandomStarTemperature(c.getColor())));
//			if(shouldRender(((AstraFrustum)frustum), p, rotation))
			apply((v, col, u) -> bufferBuilder.vertex(matrix4f, v.getX(), v.getY(), v.getZ()).color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()).uv(u.x, u.y).light(RenderHelper.FULL_BRIGHT).next(), p);
		}
		stars.bind();
		stars.upload(bufferBuilder.end());
		VertexBuffer.unbind();
	}

	private static void apply(TriConsumer<Vec3f, Color, Vec2f> gungy, VertexData data) {
		for (int i = 0; i < 4; i++) {
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

		float t1x = -y * uz + (z * uy);
		float t1y = -z * ux + (x * uz);
		float t1z = -x * uy + (y * ux);

		float t1d2 = (float) Math.sqrt(distanceSquared(t1x, t1y, t1z));

		t1x /= t1d2;
		t1y /= t1d2;
		t1z /= t1d2;

		float t2x = -y * t1z + (z * t1y);
		float t2y = -z * t1x + (x * t1z);
		float t2z = -x * t1y + (y * t1x);

		float t2d2 = (float) Math.sqrt(distanceSquared(t2x, t2y, t2z));

		t2x /= t2d2;
		t2y /= t2d2;
		t2z /= t2d2;
		t1x *= size;
		t1y *= size;
		t1z *= size;
		t2x *= size;
		t2y *= size;
		t2z *= size;

		x *= distance;
		y *= distance;
		z *= distance;

		return new VertexData(new Vec3f[]{new Vec3f(x + t1x + t2x, y + t1y + t2y, z + t1z + t2z), new Vec3f(x - t1x + t2x, y - t1y + t2y, z - t1z + t2z), new Vec3f(x - t1x - t2x, y - t1y - t2y, z - t1z - t2z), new Vec3f(x + t1x - t2x, y + t1y - t2y, z + t1z - t2z)}, new Color[]{c}, new Vec2f[]{new Vec2f(0, 1), new Vec2f(1, 1), new Vec2f(1, 0), new Vec2f(0, 0)});
	}

	private static VertexData createVertexData(Vec3d dir, Vec3d up, float size, float distance, float rotation, Color c) {
		float x = (float) dir.x;
		float y = (float) dir.y;
		float z = (float) dir.z;

		float ux = (float) up.x;
		float uy = (float) up.y;
		float uz = (float) up.z;

		float f = MathHelper.sin(rotation / 2.0F);
		float qx = x * f;
		float qy = y * f;
		float qz = z * f;
		float qw = MathHelper.cos(rotation / 2.0F);

		float t1x = -y * uz + (z * uy);
		float t1y = -z * ux + (x * uz);
		float t1z = -x * uy + (y * ux);

		Vec3d q = rotateViaQuat(t1x, t1y, t1z, qx, qy, qz, qw);
		t1x = (float) q.x;
		t1y = (float) q.y;
		t1z = (float) q.z;

		float t1d2 = (float) Math.sqrt(distanceSquared(t1x, t1y, t1z));

		t1x /= t1d2;
		t1y /= t1d2;
		t1z /= t1d2;

		float t2x = -y * t1z + (z * t1y);
		float t2y = -z * t1x + (x * t1z);
		float t2z = -x * t1y + (y * t1x);

		float t2d2 = (float) Math.sqrt(distanceSquared(t2x, t2y, t2z));

		t2x /= t2d2;
		t2y /= t2d2;
		t2z /= t2d2;
		t1x *= size;
		t1y *= size;
		t1z *= size;
		t2x *= size;
		t2y *= size;
		t2z *= size;

		x *= distance;
		y *= distance;
		z *= distance;

		return new VertexData(new Vec3f[]{new Vec3f(x + t1x + t2x, y + t1y + t2y, z + t1z + t2z), new Vec3f(x - t1x + t2x, y - t1y + t2y, z - t1z + t2z), new Vec3f(x - t1x - t2x, y - t1y - t2y, z - t1z - t2z), new Vec3f(x + t1x - t2x, y + t1y - t2y, z + t1z - t2z)}, new Color[]{c}, new Vec2f[]{new Vec2f(0, 1), new Vec2f(1, 1), new Vec2f(1, 0), new Vec2f(0, 0)});
	}

	private static @NotNull Vec3d rotateViaQuat(float x, float y, float z, float ux, float uy, float uz, float scalar) {

		float cx = -y * uz + (z * uy);
		float cy = -z * ux + (x * uz);
		float cz = -x * uy + (y * ux);

		double s1 = 2.0f * (ux * x + uy * y + uz * z);
		double s2 = scalar * scalar - (ux * ux + uy * uy + uz * uz);
		double s3 = 2.0f * scalar;

		double vpx = s1 * ux + s2 * x + s3 * cx;
		double vpy = s1 * uy + s2 * y + s3 * cy;
		double vpz = s1 * uz + s2 * z + s3 * cz;

		return new Vec3d(vpx, vpy, vpz);
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

		float t1x = -y * uz + (z * uy);
		float t1y = -z * ux + (x * uz);
		float t1z = -x * uy + (y * ux);

		float t1d2 = (float) Math.sqrt(distanceSquared(t1x, t1y, t1z));

		t1x /= t1d2;
		t1y /= t1d2;
		t1z /= t1d2;

		x = (float) pos.x;
		y = (float) pos.y;
		z = (float) pos.z;

		ux = (float) up.x;
		uy = (float) up.y;
		uz = (float) up.z;

		return new VertexData(new Vec3f[]{new Vec3f(x + t1x * endSize + ux, y + t1y * endSize + uy, z + t1z * endSize + uz), new Vec3f(x - t1x * endSize + ux, y - t1y * endSize + uy, z - t1z * endSize + uz), new Vec3f(x - t1x * beginSize, y - t1y * beginSize, z - t1z * beginSize), new Vec3f(x + t1x * beginSize, y + t1y * beginSize, z + t1z * beginSize)}, new Color[]{new Color(c.getRed(), c.getGreen(), c.getBlue(), endAlpha), new Color(c.getRed(), c.getGreen(), c.getBlue(), endAlpha), c, c}, new Vec2f[]{new Vec2f(0, 6 + vOffset), new Vec2f(1, 6 + vOffset), new Vec2f(1, 0 + vOffset), new Vec2f(0, 0 + vOffset)});
	}

	private static VertexData createVertexData(Vec3d pos, Vec3d up, float size, Color c) {
		Vec3d b = pos.normalize();
		float x = (float) b.x;
		float y = (float) b.y;
		float z = (float) b.z;

		float ux = (float) up.x;
		float uy = (float) up.y;
		float uz = (float) up.z;

		float t1x = -y * uz + (z * uy);
		float t1y = -z * ux + (x * uz);
		float t1z = -x * uy + (y * ux);

		float t1d2 = (float) Math.sqrt(distanceSquared(t1x, t1y, t1z));

		t1x /= t1d2;
		t1y /= t1d2;
		t1z /= t1d2;

		float t2x = -y * t1z + (z * t1y);
		float t2y = -z * t1x + (x * t1z);
		float t2z = -x * t1y + (y * t1x);

		float t2d2 = (float) Math.sqrt(distanceSquared(t2x, t2y, t2z));

		t2x /= t2d2;
		t2y /= t2d2;
		t2z /= t2d2;
		t1x *= size;
		t1y *= size;
		t1z *= size;
		t2x *= size;
		t2y *= size;
		t2z *= size;

		x = (float) pos.x;
		y = (float) pos.y;
		z = (float) pos.z;

		return new VertexData(new Vec3f[]{new Vec3f(x + t1x + t2x, y + t1y + t2y, z + t1z + t2z), new Vec3f(x - t1x + t2x, y - t1y + t2y, z - t1z + t2z), new Vec3f(x - t1x - t2x, y - t1y - t2y, z - t1z - t2z), new Vec3f(x + t1x - t2x, y + t1y - t2y, z + t1z - t2z)}, new Color[]{c}, new Vec2f[]{new Vec2f(0, 1), new Vec2f(1, 1), new Vec2f(1, 0), new Vec2f(0, 0)});
	}

	public static Quaternion invert(Quaternion in) {
		float invNorm = 1.0f / Math.fma(in.getX(), in.getX(), Math.fma(in.getY(), in.getY(), Math.fma(in.getZ(), in.getZ(), in.getW() * in.getW())));
		return new Quaternion(-in.getX() * invNorm, -in.getY() * invNorm, -in.getZ() * invNorm, in.getW() * invNorm);
	}

	private static float distanceSquared(float x, float y, float z) {
		return x * x + y * y + z * z;
	}


}
