package doctor4t.astronomical.client;

import carpet.script.language.Sys;
import com.sammy.lodestone.handlers.RenderHandler;
import com.sammy.lodestone.handlers.ScreenParticleHandler;
import com.sammy.lodestone.setup.LodestoneRenderLayers;
import com.sammy.lodestone.systems.rendering.VFXBuilders;
import com.sammy.lodestone.systems.rendering.particle.Easing;
import doctor4t.astronomical.client.particle.AstralFragmentParticleEmitter;
import doctor4t.astronomical.client.render.entity.FallenStarEntityRenderer;
import doctor4t.astronomical.client.render.entity.block.AstralDisplayBlockEntityRenderer;
import doctor4t.astronomical.client.render.entity.block.AstralLanternBlockEntityRenderer;
import doctor4t.astronomical.client.render.world.AstraSkyRenderer;
import doctor4t.astronomical.client.render.world.AstraWorldVFXBuilder;
import doctor4t.astronomical.client.render.world.VertexData;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.block.entity.AstralDisplayBlockEntity;
import doctor4t.astronomical.common.entity.FallenStarEntity;
import doctor4t.astronomical.common.init.*;
import doctor4t.astronomical.common.item.MarshmallowStickItem;
import doctor4t.astronomical.common.item.NanoCosmosItem;
import doctor4t.astronomical.common.item.NanoPlanetItem;
import doctor4t.astronomical.common.item.NanoRingItem;
import doctor4t.astronomical.common.screen.AstralDisplayScreen;
import doctor4t.astronomical.common.screen.AstralDisplayScreenHandler;
import doctor4t.astronomical.common.screen.PlanetColorScreen;
import doctor4t.astronomical.common.screen.RingColorScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;

import java.awt.*;
import java.util.HashMap;

public class AstronomicalClient implements ClientModInitializer {
	//https://www.youtube.com/watch?v=phWWx4NRhpE
	public static final HashMap<BlockPos, Vec3d> ORBITING_POSITIONS = new HashMap<>();

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

			// entity rendering optimization mods will be the end of me
			if (MinecraftClient.getInstance().player.hasStatusEffect(ModStatusEffects.STARGAZING) && MinecraftClient.getInstance().player.getStatusEffect(ModStatusEffects.STARGAZING).getAmplifier() > 0) {
				float tickDelta = context.tickDelta();
				MatrixStack matrices = context.matrixStack();
				Entity camera = MinecraftClient.getInstance().cameraEntity;
				Vec3d cameraPos = camera.getPos();
				context.world().getEntities().forEach(entity -> {
					if (entity instanceof FallenStarEntity fallenStarEntity) {
						double d = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
						double e = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
						double f = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());

						double x = d - MathHelper.lerp(tickDelta, camera.prevX, cameraPos.getX());
						double y = e - MathHelper.lerp(tickDelta, camera.prevY, cameraPos.getY());
						double z = f - MathHelper.lerp(tickDelta, camera.prevZ, cameraPos.getZ());

						Vec3d vec3d = context.worldRenderer().entityRenderDispatcher.getRenderer(entity).getPositionOffset(entity, tickDelta);
						double d2 = x + vec3d.getX();
						double e2 = y + vec3d.getY();
						double f2 = z + vec3d.getZ();
						matrices.push();
						matrices.translate(d2, e2-2f, f2);

						MinecraftClient client = MinecraftClient.getInstance();
						Vec3d playerPos = client.player != null ? client.player.getCameraPosVec(tickDelta) : Vec3d.ZERO;
						Vec3d diff = entity.getPos().subtract(playerPos);
						float easein = MathHelper.lerp(Easing.SINE_OUT.ease(MathHelper.clamp(entity.age / 100f, 0, 1), 0, 1, 1), 0f, 1f);
						Vec3d dirVec = new Vec3d(0, easein * 100f, 0);
						Color color = Astronomical.STAR_PURPLE.darker();
						float time = ((float) (MinecraftClient.getInstance().world.getTime() % 2400000L) + tickDelta);

						VertexData fadeoutVertexData = AstraWorldVFXBuilder.createFadeoutVertexData(diff, dirVec, 1f, 1f, color, 0, -(time % 190) / 190f);

						((AstraWorldVFXBuilder) builder.setOffset((float) ((float) -entity.getX() + playerPos.getX()), (float) ((float) -entity.getY() + playerPos.getY()), (float) ((float) -entity.getZ() + playerPos.getZ())).setAlpha(easein * (1 - MathHelper.clamp(25 / (float) diff.length(), 0, 1)))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(AstraSkyRenderer.SHIMMER)), matrices, fadeoutVertexData, builder::setPosColorTexLightmapDefaultFormat);

						diff = entity.getPos().subtract(playerPos).add(-0.08, -0.08, -0.08);

						fadeoutVertexData = AstraWorldVFXBuilder.createFadeoutVertexData(diff, dirVec, 1f, 1f, color, 0, (-(time % 190) / 190f + 0.1f) * 1.2f);

						((AstraWorldVFXBuilder) builder.setOffset((float) ((float) -entity.getX() + playerPos.getX()), (float) ((float) -entity.getY() + playerPos.getY()), (float) ((float) -entity.getZ() + playerPos.getZ())).setAlpha(easein * (1 - MathHelper.clamp(25 / (float) diff.length(), 0, 1)))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(AstraSkyRenderer.SHIMMER)), matrices, fadeoutVertexData, builder::setPosColorTexLightmapDefaultFormat);

						diff = entity.getPos().subtract(playerPos).add(0.08, 0.08, 0.08);

						fadeoutVertexData = AstraWorldVFXBuilder.createFadeoutVertexData(diff, dirVec, 1f, 1f, color, 0, (-(time % 190) / 190f + 0.6f) * 0.9f);

						((AstraWorldVFXBuilder) builder.setOffset((float) ((float) -entity.getX() + playerPos.getX()), (float) ((float) -entity.getY() + playerPos.getY()), (float) ((float) -entity.getZ() + playerPos.getZ())).setAlpha(easein * (1 - MathHelper.clamp(25 / (float) diff.length(), 0, 1)))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(AstraSkyRenderer.SHIMMER)), matrices, fadeoutVertexData, builder::setPosColorTexLightmapDefaultFormat);
						matrices.pop();
					}
				});
			}
		});
	}
}
