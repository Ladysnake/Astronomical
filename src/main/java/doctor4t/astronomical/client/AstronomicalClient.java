package doctor4t.astronomical.client;

import com.sammy.lodestone.handlers.RenderHandler;
import com.sammy.lodestone.handlers.ScreenParticleHandler;
import com.sammy.lodestone.setup.LodestoneRenderLayers;
import com.sammy.lodestone.systems.rendering.VFXBuilders;
import doctor4t.astronomical.client.particle.AstralFragmentParticleEmitter;
import doctor4t.astronomical.client.render.entity.FallenStarEntityRenderer;
import doctor4t.astronomical.client.render.entity.block.AstralDisplayBlockEntityRenderer;
import doctor4t.astronomical.client.render.entity.block.AstralLanternBlockEntityRenderer;
import doctor4t.astronomical.client.render.world.AstraSkyRenderer;
import doctor4t.astronomical.client.render.world.AstraWorldVFXBuilder;
import doctor4t.astronomical.client.render.world.VertexData;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.block.AstralDisplayBlock;
import doctor4t.astronomical.common.block.entity.AstralDisplayBlockEntity;
import doctor4t.astronomical.common.init.*;
import doctor4t.astronomical.common.item.*;
import doctor4t.astronomical.common.screen.AstralDisplayScreen;
import doctor4t.astronomical.common.screen.AstralDisplayScreenHandler;
import doctor4t.astronomical.common.screen.PlanetColorScreen;
import doctor4t.astronomical.common.screen.RingColorScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static doctor4t.astronomical.client.render.world.AstraSkyRenderer.rotateViaQuat;

public class AstronomicalClient implements ClientModInitializer {
	// astral display blockpos to astral object pos map
	public static final HashMap<BlockPos, Vec3d> ASTRAL_OBJECTS_TO_RENDER = new HashMap<>();
	public static final Vec3d UP = new Vec3d(0, 1, 0);

	// common / misc layers
	public static final RenderLayer SOLID_BALL = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE.apply(new Identifier(Astronomical.MOD_ID, "textures/astral_object/white.png"));

	// star layer
	public static final RenderLayer STAR_1 = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE_ADDITIVE.apply(new Identifier(Astronomical.MOD_ID, "textures/astral_object/star/star_1.png"));
	public static final RenderLayer STAR_2 = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE_ADDITIVE.apply(new Identifier(Astronomical.MOD_ID, "textures/astral_object/star/star_2.png"));
	public static final RenderLayer STAR_3 = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE_ADDITIVE.apply(new Identifier(Astronomical.MOD_ID, "textures/astral_object/star/star_3.png"));

	public static long lastRenderTick = 0;
	public static boolean renderStarsThisTick = false;
	VFXBuilders.WorldVFXBuilder builder;

	// centralized method to render all astral objects
	// don't forget to push the matrix stack before and pop it after!!
	public static void renderAstralObject(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, VFXBuilders.WorldVFXBuilder builder, ItemStack stackToDisplay, int sphereDetail, float time, boolean delayRender) {
		if (stackToDisplay.isOf(ModItems.NANO_PLANET)) {
			int color1 = stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color1");
			int color2 = stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color2");
			RenderLayer renderLayer = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE_TRANSPARENT.applyAndCache(NanoPlanetItem.PlanetTexture.byName(stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getString("texture")).texture);

			builder.setColor(new Color(color1))
				.setAlpha(1f)
				.renderSphere(
					vertexConsumerProvider.getBuffer(AstronomicalClient.SOLID_BALL),
					matrixStack,
					1,
					sphereDetail,
					sphereDetail);

			builder.setColor(new Color(color2))
				.setAlpha(1f)
				.renderSphere(
					vertexConsumerProvider.getBuffer(renderLayer),
					matrixStack,
					1,
					sphereDetail,
					sphereDetail);
		} else if (stackToDisplay.isOf(ModItems.NANO_STAR)) {
			int color = Astronomical.getStarColorForTemperature(stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("temperature"));

			builder.setColor(new Color(color).darker())
				.setAlpha(1f)
				.renderSphere(
					vertexConsumerProvider.getBuffer(AstronomicalClient.SOLID_BALL),
					matrixStack,
					1,
					sphereDetail,
					sphereDetail);

			for (int layer = 1; layer <= 6; layer++) {
				float speedDiv = 5f;
				float scaleDiv = 45f;

				matrixStack.push();
				matrixStack.scale(1f + layer / scaleDiv, 1f + layer / scaleDiv, 1f + layer / scaleDiv);
				RenderLayer renderLayer = AstronomicalClient.STAR_1;
				switch (layer % 6) {
					case 0 -> matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(time / speedDiv));
					case 1 -> {
						renderLayer = AstronomicalClient.STAR_2;
						matrixStack.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(time / speedDiv));
					}
					case 2 -> {
						renderLayer = AstronomicalClient.STAR_3;
						matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(time / speedDiv));
					}
					case 3 -> matrixStack.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(time / speedDiv));
					case 4 -> {
						renderLayer = AstronomicalClient.STAR_2;
						matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(time / speedDiv));
					}
					case 5 -> {
						renderLayer = AstronomicalClient.STAR_3;
						matrixStack.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(time / speedDiv));
					}
				}
				builder.setColor(new Color(color))
					.setAlpha(.3f).renderSphere(
						(delayRender ? RenderHandler.EARLY_DELAYED_RENDER : vertexConsumerProvider).getBuffer(renderLayer),
						matrixStack,
						1,
						sphereDetail,
						sphereDetail);
				matrixStack.pop();
			}
		} else if (stackToDisplay.isOf(ModItems.NANO_COSMOS)) {
			RenderLayer renderLayer = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE.applyAndCache(NanoCosmosItem.CosmosTexture.byName(stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getString("texture")).texture);

			builder.setColor(new Color(0xFFFFFF))
				.setAlpha(1f)
				.renderSphere(
					vertexConsumerProvider.getBuffer(renderLayer),
					matrixStack,
					-1,
					sphereDetail,
					sphereDetail);
		} else if (stackToDisplay.isOf(ModItems.NANO_RING)) {
			int color = stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color");
			NanoRingItem.RingTexture ringTexture = NanoRingItem.RingTexture.byName(stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getString("texture"));
			RenderLayer renderLayer = (ringTexture == NanoRingItem.RingTexture.EYE_OF_THE_UNIVERSE ? LodestoneRenderLayers.ADDITIVE_TEXTURE : LodestoneRenderLayers.TRANSPARENT_TEXTURE).applyAndCache(ringTexture.texture);
			float scale = (ringTexture == NanoRingItem.RingTexture.EYE_OF_THE_UNIVERSE ? 2f : 1f);

			matrixStack.scale(scale, scale, scale);
			matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90f));

			builder.setColor(new Color(color))
				.setAlpha(1f)
				.renderQuad(
					(delayRender ? RenderHandler.EARLY_DELAYED_RENDER : vertexConsumerProvider).getBuffer(renderLayer),
					matrixStack,
					1
				);

			matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180f));

			builder.setColor(new Color(color))
				.setAlpha(1f)
				.renderQuad(
					(delayRender ? RenderHandler.EARLY_DELAYED_RENDER : vertexConsumerProvider).getBuffer(renderLayer),
					matrixStack,
					1
				);
		}
	}

	@Override
	public void onInitializeClient(ModContainer mod) {
		// init model layers
//		ModModelLayers.initialize();

		// entity renderers registration
		EntityRendererRegistry.register(ModEntities.FALLEN_STAR, FallenStarEntityRenderer::new);

		// block entity renderers registration
		BlockEntityRendererFactories.register(ModBlockEntities.ASTRAL_DISPLAY, AstralDisplayBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(ModBlockEntities.ASTRAL_LANTERN, AstralLanternBlockEntityRenderer::new);

		// particle renderers registration
		ModParticles.registerFactories();

		// block special layers
		BlockRenderLayerMap.put(RenderLayer.getCutout(), ModBlocks.MARSHMALLOW_CAN, ModBlocks.STARMALLOW_CAN, ModBlocks.ASTRAL_LANTERN);

		HandledScreens.register(Astronomical.ASTRAL_DISPLAY_SCREEN_HANDLER, AstralDisplayScreen::new);
		HandledScreens.register(Astronomical.PLANET_COLOR_SCREEN_HANDLER, PlanetColorScreen::new);
		HandledScreens.register(Astronomical.RING_COLOR_SCREEN_HANDLER, RingColorScreen::new);

		ClientPlayNetworking.registerGlobalReceiver(Astronomical.id("astral_display"), (minecraftClient, playNetworkHandler, packetByteBuf, packetSender) -> {
			var pos = packetByteBuf.readBlockPos();
			var yLevel = packetByteBuf.readDouble();
			var rotSpeed = packetByteBuf.readDouble();
			var spin = packetByteBuf.readDouble();
			minecraftClient.execute(() -> {
				World world = minecraftClient.world;
				var player = minecraftClient.player;
				if (player != null && world != null && world.getBlockEntity(pos) instanceof AstralDisplayBlockEntity display) {
					if (player.currentScreenHandler instanceof AstralDisplayScreenHandler handler) {
						handler.entity = display;
						display.yLevel.setValue(yLevel);
						display.rotSpeed.setValue(rotSpeed);
						display.spin.setValue(spin);
						if (minecraftClient.currentScreen instanceof AstralDisplayScreen screen) {
							screen.addSliders();
						}
					}
				}
			});
		});

		ModelPredicateProviderRegistry.register(ModItems.MARSHMALLOW_STICK, Astronomical.id("marshmallow"), (stack, world, entity, seed) -> MarshmallowStickItem.CookState.getCookState(stack).ordinal() / (float) MarshmallowStickItem.CookState.values().length);
		ModelPredicateProviderRegistry.register(ModItems.STARMALLOW_STICK, Astronomical.id("marshmallow"), (stack, world, entity, seed) -> MarshmallowStickItem.CookState.getCookState(stack).ordinal() / (float) MarshmallowStickItem.CookState.values().length);

		ScreenParticleHandler.registerItemParticleEmitter(ModItems.ASTRAL_FRAGMENT, AstralFragmentParticleEmitter::particleTick);

		this.builder = new AstraWorldVFXBuilder().setPosColorTexLightmapDefaultFormat();
		WorldRenderEvents.START.register(context -> {
			AstronomicalClient.renderStarsThisTick = (context.world().getTime() != AstronomicalClient.lastRenderTick);
			AstronomicalClient.lastRenderTick = context.world().getTime();
		});

		// RENDER ASTRAL OBJECTS FROM ASTRAL DISPLAYS
		WorldRenderEvents.BEFORE_ENTITIES.register(context -> {
			MatrixStack matrices = context.matrixStack();
			World world = context.world();
			VertexConsumerProvider vertexConsumerProvider = context.consumers();
			float tickDelta = MinecraftClient.getInstance().isPaused() ? 0f : MinecraftClient.getInstance().getTickDelta();
			float time = ((float) (MinecraftClient.getInstance().world.getTime() % 2400000L) + tickDelta);

			ArrayList<BlockPos> displaysToRemove = new ArrayList<>();

			ASTRAL_OBJECTS_TO_RENDER.forEach((blockPos, vec3d) -> {
				BlockState blockState = world.getBlockState(blockPos);
				BlockEntity blockEntity = world.getBlockEntity(blockPos);

				if (blockEntity instanceof AstralDisplayBlockEntity astralDisplayBlockEntity && blockState.isOf(ModBlocks.ASTRAL_DISPLAY) && blockState.get(AstralDisplayBlock.POWERED)) {

					double distance;
					float selfRotation = (float) (-time * (astralDisplayBlockEntity.spin.getScaledValue()));
					double speedModifier;

					Vec3d bePos = Vec3d.ofCenter(blockPos);
					Vec3d parentPos;
					Vec3d orbitCenter;
					Vec3d astralPos = Vec3d.ofCenter(blockPos).add(0, astralDisplayBlockEntity.yLevel.getScaledValue(), 0);

					Camera camera = context.camera();
					Vec3d cameraPos = camera.getPos();

					matrices.push();

					// if connected child, render object orbiting around parent
					if (astralDisplayBlockEntity.getParentPos() != null
						&& astralDisplayBlockEntity.getWorld().getBlockState(astralDisplayBlockEntity.getParentPos()).isOf(ModBlocks.ASTRAL_DISPLAY)
						&& astralDisplayBlockEntity.getWorld().getBlockEntity(astralDisplayBlockEntity.getParentPos()) instanceof AstralDisplayBlockEntity) {
//						double d = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
//						double e = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
//						double f = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());

						matrices.push();
						double x = bePos.getX() - cameraPos.getX();
						double y = bePos.getY() - cameraPos.getY();
						double z = bePos.getZ() - cameraPos.getZ();
						matrices.translate(x, y, z);

						parentPos = Vec3d.ofCenter(astralDisplayBlockEntity.getParentPos());
						orbitCenter = AstronomicalClient.ASTRAL_OBJECTS_TO_RENDER.getOrDefault(astralDisplayBlockEntity.getParentPos(), Vec3d.ofCenter(astralDisplayBlockEntity.getParentPos()));

						// astral link connecting displays
						double v = bePos.distanceTo(parentPos);
						for (int k = 0; k < v; k++) {
							Vec3d nextPos = Vec3d.ofCenter(blockPos.offset(blockState.get(AstralDisplayBlock.FACING), k));
							BlockPos nextNextBP = blockPos.offset(blockState.get(AstralDisplayBlock.FACING), k + 1);
							Vec3d nextNextPos = Vec3d.ofCenter(nextNextBP);

							VFXBuilders.WorldVFXBuilder builder = new AstraWorldVFXBuilder().setPosColorTexLightmapDefaultFormat();
							MinecraftClient client = MinecraftClient.getInstance();
							Vec3d playerPos = client.player != null ? client.player.getCameraPosVec(tickDelta) : Vec3d.ZERO;
							Vec3d diff = nextPos.subtract(playerPos);
							Vec3d dirVec = nextNextPos.subtract(nextPos);
							Color color = Astronomical.STAR_PURPLE;
							VertexData d = AstraWorldVFXBuilder.createFadeoutVertexData(diff, dirVec, .1f, .1f, color, color.getAlpha(), -(time % 190) / 190f);

							((AstraWorldVFXBuilder) builder.setOffset((float) ((float) -bePos.getX() + playerPos.getX()), (float) ((float) -bePos.getY() + playerPos.getY()), (float) ((float) -bePos.getZ() + playerPos.getZ()))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(AstraSkyRenderer.SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);

							diff = nextPos.subtract(playerPos).add(-0.0, -0.02, -0.0);

							d = AstraWorldVFXBuilder.createFadeoutVertexData(diff, dirVec, .1f, .1f, color, color.getAlpha(), (-(time % 190) / 190f + 0.1f) * 4f);

							((AstraWorldVFXBuilder) builder.setOffset((float) ((float) -bePos.getX() + playerPos.getX()), (float) ((float) -bePos.getY() + playerPos.getY()), (float) ((float) -bePos.getZ() + playerPos.getZ()))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(AstraSkyRenderer.SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);

							diff = nextPos.subtract(playerPos).add(0.0, 0.02, 0.0);

							d = AstraWorldVFXBuilder.createFadeoutVertexData(diff, dirVec, .1f, .1f, color, color.getAlpha(), (-(time % 190) / 190f + 0.6f) * 2f);

							((AstraWorldVFXBuilder) builder.setOffset((float) ((float) -bePos.getX() + playerPos.getX()), (float) ((float) -bePos.getY() + playerPos.getY()), (float) ((float) -bePos.getZ() + playerPos.getZ()))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(AstraSkyRenderer.SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);

							BlockState bs = astralDisplayBlockEntity.getWorld().getBlockState(nextNextBP);
							if (bs.isOf(ModBlocks.ASTRAL_DISPLAY) && bs.get(AstralDisplayBlock.POWERED)) {
								break;
							}
						}

						matrices.pop();

						// update orbit position hashmap
						distance = parentPos.distanceTo(bePos);
						speedModifier = astralDisplayBlockEntity.rotSpeed.getScaledValue();

						float offset = switch (blockState.get(AstralDisplayBlock.FACING)) {
							case SOUTH -> (float) Math.PI;
							case WEST -> (float) Math.PI / 2f;
							case EAST -> (float) -Math.PI / 2f;
							default -> 0.0F;
						};
						astralPos = new Vec3d(orbitCenter.getX() + (Math.sin((time * speedModifier) + offset) * distance), orbitCenter.getY(), orbitCenter.getZ() + (Math.cos((time * speedModifier) + offset) * distance));
					}
					AstronomicalClient.ASTRAL_OBJECTS_TO_RENDER.put(blockPos, astralPos);

					for (int slot = 0; slot < AstralDisplayBlockEntity.SIZE; slot++) {
						ItemStack stackToDisplay = astralDisplayBlockEntity.getStack(slot);
						if (stackToDisplay.getItem() instanceof NanoAstralObjectItem) {
							float scale = stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("size") * .5f;
							int circlePrecision = MathHelper.clamp((int) scale * 2, 15, 50);

							matrices.push();

							double x = astralPos.getX() - cameraPos.getX();
							double y = astralPos.getY() - cameraPos.getY();
							double z = astralPos.getZ() - cameraPos.getZ();
							matrices.translate(x, y, z);

							matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(selfRotation));
							matrices.scale(scale, scale, scale);

							AstronomicalClient.renderAstralObject(matrices, vertexConsumerProvider, this.builder, stackToDisplay, circlePrecision, time, true);

							matrices.pop();
						}
					}

					matrices.pop();
				} else {
					displaysToRemove.add(blockPos);
				}
			});

			for (BlockPos blockPos : displaysToRemove) {
				ASTRAL_OBJECTS_TO_RENDER.remove(blockPos);
			}
		});
	}

	public static VertexData createVertexData(Vec3d dir, Vec3d up, float size, float rotation, Color c) {
		float x = (float) dir.x;
		float y = (float) dir.y;
		float z = (float) dir.z;

		if (Math.abs(dir.dotProduct(up)) >= 1) {
			up = new Vec3d(0, 0, 1);
		}

		float ux = (float) up.x;
		float uy = (float) up.y;
		float uz = (float) up.z;

		float f = MathHelper.sin(rotation / 2.0F);
		float qx = x * f;
		float qy = y * f;
		float qz = z * f;
		float qw = MathHelper.cos(rotation / 2.0F);

		float t1x = y * uz - (z * uy);
		float t1y = z * ux - (x * uz);
		float t1z = x * uy - (y * ux);

		Vec3d q = rotateViaQuat(t1x, t1y, t1z, qx, qy, qz, qw);
		t1x = (float) q.x;
		t1y = (float) q.y;
		t1z = (float) q.z;

		float t1d2 = (float) Math.sqrt(AstraWorldVFXBuilder.distanceSquared(t1x, t1y, t1z));

		t1x /= t1d2;
		t1y /= t1d2;
		t1z /= t1d2;

		float t2x = y * t1z - (z * t1y);
		float t2y = z * t1x - (x * t1z);
		float t2z = x * t1y - (y * t1x);

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
		x *= 0.501f;
		y *= 0.501f;
		z *= 0.501f;

		return new VertexData(new Vec3f[]{new Vec3f(x + t1x + t2x, y + t1y + t2y, z + t1z + t2z), new Vec3f(x - t1x + t2x, y - t1y + t2y, z - t1z + t2z), new Vec3f(x - t1x - t2x, y - t1y - t2y, z - t1z - t2z), new Vec3f(x + t1x - t2x, y + t1y - t2y, z + t1z - t2z)}, new Color[]{c, c, c, c}, new Vec2f[]{new Vec2f(0, 1), new Vec2f(1, 1), new Vec2f(1, 0), new Vec2f(0, 0)});
	}

}
