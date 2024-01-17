package doctor4t.astronomical.client.render.world;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
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
import net.minecraft.util.math.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.helpers.RenderHelper;
import team.lodestar.lodestone.setup.LodestoneParticles;
import team.lodestar.lodestone.setup.LodestoneRenderLayers;
import team.lodestar.lodestone.setup.LodestoneShaders;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;
import team.lodestar.lodestone.systems.rendering.particle.Easing;
import team.lodestar.lodestone.systems.rendering.particle.WorldParticleBuilder;
import team.lodestar.lodestone.systems.rendering.particle.data.ColorParticleData;
import team.lodestar.lodestone.systems.rendering.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.rendering.particle.data.SpinParticleData;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

import java.awt.*;
import java.util.List;

public class AstraSkyRenderer {
	public static final Identifier SHIMMER = Astronomical.id("textures/vfx/shimmer.png");
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
		Quaternionf rotation = Axis.Z_POSITIVE.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0F);
//		Vec3d up = rotateViaQuat(UP, invert(rotation));
		//rotation.normalize();

		matrices.push();
		matrices.multiply(rotation);

		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		RenderSystem.setShaderColor(1, 1, 1, world.getStarBrightness(tickDelta));
		RenderSystem.setShaderTexture(0, Star.TEMPTEX);

		stars.bind();
		stars.draw(matrices.peek().getModel(), projectionMatrix, LodestoneShaders.LODESTONE_TEXTURE.getInstance().get());
		VertexBuffer.unbind();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		Matrix4f matrix4f = matrices.peek().getModel();

		// interactable stars
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
		RenderSystem.setShader(LodestoneShaders.LODESTONE_TEXTURE.getInstance());
		RenderSystem.setShaderTexture(0, InteractableStar.INTERACTABLE_TEX);
		float scale = .2f;
		AstraSkyComponent com = world.getComponent(AstraCardinalComponents.SKY);

		List<CelestialObject> list = com.getCelestialObjects().stream().filter(celestialObject -> celestialObject instanceof InteractableStar interactableStar && interactableStar.supernovaTicks <= InteractableStar.HOLD_COLLAPSE_TICKS).toList();
		for (CelestialObject c : list) {
			Vec3d vector = c.getDirectionVector();
			float rot1 = (time + c.getRandomOffset() + tickDelta) / 50f;
			float rot2 = (time + c.getRandomOffset() + tickDelta + 10) / 100f;
			float rot3 = -(time + c.getRandomOffset() + tickDelta + 20) / 50f;
			float rot4 = -(time + c.getRandomOffset() + tickDelta + 20) / 100f;

			int alpha = (int) (c.getAlpha() * 255);

			VertexData p = createVertexData(vector, UP, (scale + 0.1f * ((1 + MathHelper.sin(rot1)) / 2f)) * .5f * c.getSize(), 95, rot1, Color.WHITE);
			apply((v, col, u) -> bufferBuilder.vertex(matrix4f, v.x(), v.y(), v.z()).color(col.getRed(), col.getGreen(), col.getBlue(), alpha).uv(u.x, u.y).light(RenderHelper.FULL_BRIGHT).next(), p);
			VertexData p2 = createVertexData(vector, UP, (scale + 0.2f * ((1 + MathHelper.sin(rot2)) / 2f)) * .5f * c.getSize(), 95, rot2, Color.WHITE);
			apply((v, col, u) -> bufferBuilder.vertex(matrix4f, v.x(), v.y(), v.z()).color(col.getRed(), col.getGreen(), col.getBlue(), alpha).uv(u.x, u.y).light(RenderHelper.FULL_BRIGHT).next(), p2);
			VertexData p3 = createVertexData(vector, UP, (scale + 0.3f * ((1 + MathHelper.sin(rot3)) / 2f)) * .5f * c.getSize(), 95, rot3, Color.WHITE);
			apply((v, col, u) -> bufferBuilder.vertex(matrix4f, v.x(), v.y(), v.z()).color(col.getRed(), col.getGreen(), col.getBlue(), alpha).uv(u.x, u.y).light(RenderHelper.FULL_BRIGHT).next(), p3);
			VertexData p4 = createVertexData(vector, UP, (scale + 0.4f * ((1 + MathHelper.sin(rot4)) / 2f)) * .5f * c.getSize(), 95, rot4, Color.WHITE);
			apply((v, col, u) -> bufferBuilder.vertex(matrix4f, v.x(), v.y(), v.z()).color(col.getRed(), col.getGreen(), col.getBlue(), alpha).uv(u.x, u.y).light(RenderHelper.FULL_BRIGHT).next(), p4);
		}
		drawWithShader(bufferBuilder.end());

		// supernovae explosions
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
		RenderSystem.setShaderTexture(0, InteractableStar.SUPERNOVA_TEX);

		list = com.getCelestialObjects().stream().filter(celestialObject -> celestialObject instanceof InteractableStar interactableStar && interactableStar.supernovaTicks >= InteractableStar.HOLD_COLLAPSE_TICKS).toList();
		for (CelestialObject c : list) {
			Color supernovaColor = new Color(c.getColor());
			Vec3d vector = c.getDirectionVector();

			int alpha = (int) (c.getAlpha() * 255);
			float yuh = 0;

			for (int i = 0; i < 5; i++) {
				VertexData p = createVertexData(vector, UP, (scale + i * .02f) * c.getSize(), 95, yuh, Color.WHITE);
				apply((v, col, u) -> bufferBuilder.vertex(matrix4f, v.x(), v.y(), v.z()).color(supernovaColor.getRed(), supernovaColor.getGreen(), supernovaColor.getBlue(), alpha).uv(u.x, u.y).light(RenderHelper.FULL_BRIGHT).next(), p);

				yuh += c.getRandomOffset() / 10f;
			}
		}
		drawWithShader(bufferBuilder.end());

		// supernovae explosions dust
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
		RenderSystem.setShaderTexture(0, InteractableStar.SUPERNOVA_DUST_TEX);

		for (CelestialObject c : list) {
			Color supernovaColor = new Color(c.getColor());
			Vec3d vector = c.getDirectionVector();

			int alpha = (int) (c.getAlpha() * 255);
			int yuh = 0;

			for (int i = 0; i < 5; i++) {
				VertexData p = createVertexData(vector, UP, (scale * (i)) * c.getSize(), 95, yuh, Color.WHITE);
				apply((v, col, u) -> bufferBuilder.vertex(matrix4f, v.x(), v.y(), v.z()).color(supernovaColor.getRed(), supernovaColor.getGreen(), supernovaColor.getBlue(), alpha).uv(u.x, u.y).light(RenderHelper.FULL_BRIGHT).next(), p);
				yuh += c.getRandomOffset();
			}
		}
		drawWithShader(bufferBuilder.end());

		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
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
			vertexBuffer.draw(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), LodestoneShaders.LODESTONE_TEXTURE.instance);
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
				VertexData d;

				// trail
				Vec3d directionalVector = s.progress <= s.ticksUntilLanded ? s.startDirection.multiply(MathHelper.lerp((float) s.progress / s.ticksUntilLanded, 10000, 100)) : s.startDirection.multiply(MathHelper.lerp((s.progress - s.ticksUntilLanded) / ((s.ticksUntilLanded * Starfall.ANIMATION_EXTENSION) - s.ticksUntilLanded), 100, 0));
				Vec3d pos = one;

				Vec3d diff = pos.subtract(playerPos);
				Color color = Astronomical.STAR_PURPLE;

				d = AstraWorldVFXBuilder.createFadeoutVertexData(diff, directionalVector, 0.25f, 0f, color, 0, -(world.getTime() + tickDelta % 190) / 190f);

				((AstraWorldVFXBuilder) builder.setAlpha(1 - MathHelper.clamp(8 / (float) diff.length(), 0, 1))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);

				diff = pos.subtract(playerPos).add(-0.08, -0.08, -0.08);

				d = AstraWorldVFXBuilder.createFadeoutVertexData(diff, directionalVector, 0.25f, 0f, color, 0, (-(world.getTime() + tickDelta % 190) / 190f + 0.1f) * 1.2f);

				((AstraWorldVFXBuilder) builder.setAlpha(1 - MathHelper.clamp(8 / (float) diff.length(), 0, 1))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);

				diff = pos.subtract(playerPos).add(0.08, 0.08, 0.08);

				d = AstraWorldVFXBuilder.createFadeoutVertexData(diff, directionalVector, 0.25f, 0f, color, 0, (-(world.getTime() + tickDelta % 190) / 190f + 0.6f) * 0.9f);

				((AstraWorldVFXBuilder) builder.setAlpha(1 - MathHelper.clamp(8 / (float) diff.length(), 0, 1))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);


				if (s.progress == s.ticksUntilLanded && !s.hasPlayedExplosion()) {
					double h = MinecraftClient.getInstance().player.getPos().distanceTo(new Vec3d(one.getX(), one.getY(), one.getZ()));
					if (h < 300) {
						h /= 300;
						h *= (MinecraftClient.getInstance().cameraEntity.getRotationVecClient().dotProduct(new Vec3d(one.getX(), one.getY(), one.getZ()).subtract(MinecraftClient.getInstance().player.getPos()).normalize()) + 1) / 2;

						// todo lmao
//						ScreenParticleBuilder b2 = ScreenParticleBuilder.create(LodestoneScreenParticles.SPARKLE).setAlpha((float) h, 0f).setScale(100000f).setColor(color, color).setLifetime(10).overrideRenderOrder(ScreenParticle.RenderOrder.AFTER_EVERYTHING);
//						b2.repeat(0, 0, 1);

						ScreenshakeHandler.addScreenshake(new ScreenshakeInstance((int) (50 * h)).setIntensity(0f, 1f, 0f).setEasing(Easing.EXPO_OUT, Easing.SINE_OUT));
					}

					for (int i = 0; i < 2; i++) {
						WorldParticleBuilder.create(ModParticles.STAR_IMPACT_FLARE)
								.setScaleData(GenericParticleData.create(80f).build())
								.setColorData(ColorParticleData.create(color, color).build())
								.setMotion(0, 0, 0)
								.setTransparencyData(GenericParticleData.create(0f, 1f, 0f).setEasing(Easing.CIRC_OUT).build())
								.enableNoClip()
								.setLifetime(10)
								.spawn(world, s.endPos.getX(), s.endPos.getY(), s.endPos.getZ());
					}

					for (int i = 0; i < 20; ++i) {
						float size = world.random.nextFloat() * 20;
						Vector3f randomMotion = new Vector3f((float) (world.random.nextGaussian()), 0, (float) (world.random.nextGaussian()));
						randomMotion.normalize();
						randomMotion.mul(1 / 8f);
						float randomSpin = (float) (world.random.nextGaussian() * .01f);

						WorldParticleBuilder.create(ModParticles.STAR_IMPACT_EXPLOSION)
								.setScaleData(GenericParticleData.create(0f, size).setCoefficient(20f).setEasing(Easing.EXPO_OUT).build())
								.setColorData(ColorParticleData.create(color, color).build())
								.addActor(genericParticle -> {
									float motionAge = genericParticle.getCurve(1);
									float x = MathHelper.lerp(Easing.LINEAR.ease(motionAge, 0, 1, 1), randomMotion.x(), 0);
									float y = MathHelper.lerp(Easing.LINEAR.ease(motionAge, 0, 1, 1), randomMotion.y(), 0);
									float z = MathHelper.lerp(Easing.LINEAR.ease(motionAge, 0, 1, 1), randomMotion.z(), 0);
									genericParticle.setParticleVelocity(new Vec3d(x, y, z));
								})
								.setTransparencyData(GenericParticleData.create(0.1f, 0f).setEasing(Easing.SINE_OUT).build())
								.setSpinData(SpinParticleData.create(randomSpin, randomSpin / 100f).setEasing(Easing.SINE_IN).build())
								.enableNoClip()
								.setLifetime(50 + (i))
								.spawn(world, s.endPos.getX(), s.endPos.getY(), s.endPos.getZ());

						// bloom
						WorldParticleBuilder.create(ModParticles.STAR_IMPACT_EXPLOSION)
								.setScaleData(GenericParticleData.create(0f, size * 2f).setCoefficient(20f).setEasing(Easing.EXPO_OUT).build())
								.addActor(genericParticle -> {
									float motionAge = genericParticle.getCurve(1);
									float x = MathHelper.lerp(Easing.LINEAR.ease(motionAge, 0, 1, 1), randomMotion.x(), 0);
									float y = MathHelper.lerp(Easing.LINEAR.ease(motionAge, 0, 1, 1), randomMotion.y(), 0);
									float z = MathHelper.lerp(Easing.LINEAR.ease(motionAge, 0, 1, 1), randomMotion.z(), 0);
									genericParticle.setParticleVelocity(new Vec3d(x, y, z));
								})
								.setTransparencyData(GenericParticleData.create(0.005f, 0f).setEasing(Easing.SINE_OUT).build())
								.setSpinData(SpinParticleData.create(randomSpin, randomSpin / 100f).setEasing(Easing.SINE_IN).build())
								.enableNoClip()
								.setLifetime(50 * (i))
								.spawn(world, s.endPos.getX(), s.endPos.getY(), s.endPos.getZ());
					}

					for (int i = 0; i < 100; ++i) {
						float size = .2f + world.random.nextFloat();
						Vector3f randomMotion = new Vector3f((float) world.random.nextGaussian(), (float) world.random.nextGaussian(), (float) world.random.nextGaussian());
						randomMotion.normalize();
						randomMotion.mul(1f);
						float randomSpin = (float) (world.random.nextGaussian() * .1f);

						WorldParticleBuilder.create(LodestoneParticles.TWINKLE_PARTICLE)
								.setScaleData(GenericParticleData.create(size).setEasing(Easing.EXPO_OUT).build())
								.setColorData(ColorParticleData.create(color, color).build())
								.addActor(genericParticle -> {
									float motionAge = genericParticle.getCurve(1);
									float x = MathHelper.lerp(Easing.LINEAR.ease(motionAge, 0, 1, 1), randomMotion.x(), 0);
									float y = MathHelper.lerp(Easing.LINEAR.ease(motionAge, 0, 1, 1), randomMotion.y(), 0);
									float z = MathHelper.lerp(Easing.LINEAR.ease(motionAge, 0, 1, 1), randomMotion.z(), 0);
									genericParticle.setParticleVelocity(new Vec3d(x, y, z));
								})
								.setTransparencyData(GenericParticleData.create(1f, 0f).setEasing(Easing.SINE_OUT).build())
								.setSpinData(SpinParticleData.create(randomSpin, randomSpin / 100f).setEasing(Easing.SINE_IN).build())
								.enableNoClip()
								.setLifetime(20 + world.random.nextInt(20))
								.spawn(world, s.endPos.getX(), s.endPos.getY(), s.endPos.getZ());
					}

					s.setPlayedExplosion(true);
				}
			}
		}
		matrices.pop();
	}

	public static void renderToStarBuffer(MatrixStack matrices, VertexConsumerProvider provider, Matrix4f projectionMatrix, float tickDelta, ClientWorld world, MinecraftClient client, List<CelestialObject> objects) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		RenderSystem.setShader(LodestoneShaders.LODESTONE_TEXTURE.getInstance());
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
		Matrix4f matrix4f = matrices.peek().getModel();

		if (stars != null) {
			stars.close();
		}
		stars = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);

		for (CelestialObject c : objects.stream().filter(p -> !p.canInteract()).toList()) {
			Vec3d vector = c.getDirectionVector();

			VertexData p = createVertexData(vector, UP, c.getSize() + 0.5f, 100, new Color(Astronomical.getRandomStarTemperature(c.getColor())));
//			if(shouldRender(((AstraFrustum)frustum), p, rotation))
			apply((v, col, u) -> bufferBuilder.vertex(matrix4f, v.x(), v.y(), v.z()).color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()).uv(u.x, u.y).light(RenderHelper.FULL_BRIGHT).next(), p);
		}
		stars.bind();
		stars.upload(bufferBuilder.end());
		VertexBuffer.unbind();
	}

	private static void apply(TriConsumer<Vector3f, Color, Vec2f> gungy, VertexData data) {
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

		float t1d2 = (float) Math.sqrt(AstraWorldVFXBuilder.distanceSquared(t1x, t1y, t1z));

		t1x /= t1d2;
		t1y /= t1d2;
		t1z /= t1d2;

		float t2x = -y * t1z + (z * t1y);
		float t2y = -z * t1x + (x * t1z);
		float t2z = -x * t1y + (y * t1x);

		float t2d2 = (float) Math.sqrt(AstraWorldVFXBuilder.distanceSquared(t2x, t2y, t2z));

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

		return new VertexData(new Vector3f[]{new Vector3f(x + t1x + t2x, y + t1y + t2y, z + t1z + t2z), new Vector3f(x - t1x + t2x, y - t1y + t2y, z - t1z + t2z), new Vector3f(x - t1x - t2x, y - t1y - t2y, z - t1z - t2z), new Vector3f(x + t1x - t2x, y + t1y - t2y, z + t1z - t2z)}, new Color[]{c}, new Vec2f[]{new Vec2f(0, 1), new Vec2f(1, 1), new Vec2f(1, 0), new Vec2f(0, 0)});
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

		float t1d2 = (float) Math.sqrt(AstraWorldVFXBuilder.distanceSquared(t1x, t1y, t1z));

		t1x /= t1d2;
		t1y /= t1d2;
		t1z /= t1d2;

		float t2x = -y * t1z + (z * t1y);
		float t2y = -z * t1x + (x * t1z);
		float t2z = -x * t1y + (y * t1x);

		float t2d2 = (float) Math.sqrt(AstraWorldVFXBuilder.distanceSquared(t2x, t2y, t2z));

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

		return new VertexData(new Vector3f[]{new Vector3f(x + t1x + t2x, y + t1y + t2y, z + t1z + t2z), new Vector3f(x - t1x + t2x, y - t1y + t2y, z - t1z + t2z), new Vector3f(x - t1x - t2x, y - t1y - t2y, z - t1z - t2z), new Vector3f(x + t1x - t2x, y + t1y - t2y, z + t1z - t2z)}, new Color[]{c}, new Vec2f[]{new Vec2f(0, 1), new Vec2f(1, 1), new Vec2f(1, 0), new Vec2f(0, 0)});
	}

	public static @NotNull Vec3d rotateViaQuat(float x, float y, float z, float ux, float uy, float uz, float scalar) {

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


}
