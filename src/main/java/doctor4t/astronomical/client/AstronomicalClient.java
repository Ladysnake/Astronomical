package doctor4t.astronomical.client;

import com.sammy.lodestone.handlers.RenderHandler;
import com.sammy.lodestone.setup.LodestoneRenderLayers;
import com.sammy.lodestone.systems.rendering.VFXBuilders;
import doctor4t.astronomical.client.render.entity.block.AstralDisplayBlockEntityRenderer;
import doctor4t.astronomical.client.render.world.AstraWorldVFXBuilder;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.block.entity.AstralDisplayBlockEntity;
import doctor4t.astronomical.common.init.ModBlockEntities;
import doctor4t.astronomical.common.init.ModItems;
import doctor4t.astronomical.common.init.ModParticles;
import doctor4t.astronomical.common.item.MarshmallowStickItem;
import doctor4t.astronomical.common.item.NanoCosmosItem;
import doctor4t.astronomical.common.item.NanoPlanetItem;
import doctor4t.astronomical.common.item.NanoRingItem;
import doctor4t.astronomical.common.screen.AstralDisplayScreen;
import doctor4t.astronomical.common.screen.AstralDisplayScreenHandler;
import doctor4t.astronomical.common.screen.PlanetColorScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

import java.awt.*;
import java.util.HashMap;

public class AstronomicalClient implements ClientModInitializer {
	//https://www.youtube.com/watch?v=phWWx4NRhpE
	public static final HashMap<BlockPos, Vec3d> ORBITING_POSITIONS = new HashMap<>();

	// common / misc layers
	public static final RenderLayer ASTRAL_DISPLAY_LINK = LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(new Identifier(Astronomical.MOD_ID, "textures/vfx/astral_display_link.png"));
	public static final RenderLayer SOLID_BALL = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE.apply(new Identifier(Astronomical.MOD_ID, "textures/astral_object/white.png"));

	// star layer
	public static final RenderLayer STAR_1 = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE_ADDITIVE.apply(new Identifier(Astronomical.MOD_ID, "textures/astral_object/star/star_1.png"));
	public static final RenderLayer STAR_2 = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE_ADDITIVE.apply(new Identifier(Astronomical.MOD_ID, "textures/astral_object/star/star_2.png"));
	public static final RenderLayer STAR_3 = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE_ADDITIVE.apply(new Identifier(Astronomical.MOD_ID, "textures/astral_object/star/star_3.png"));

	@Override
	public void onInitializeClient(ModContainer mod) {
		// init model layers
//		ModModelLayers.initialize();

		// entity renderers registration
		BlockEntityRendererFactories.register(ModBlockEntities.ASTRAL_DISPLAY, AstralDisplayBlockEntityRenderer::new);
//		EntityRendererRegistry.register(ModEntities.STAR, StarEntityRenderer::new);

		// particle renderers registration
		ModParticles.registerFactories();

		// block special layers
//		BlockRenderLayerMap.put(RenderLayer.getCutout(), ModBlocks.LOCKER, ModBlocks.CLOSED_LOCKER, ModBlocks.WALKWAY, ModBlocks.WALKWAY_STAIRS, ModBlocks.ABYSSTEEL_CHAIN, ModBlocks.ANGLERWEED, ModBlocks.ANGLERWEED_PLANT, ModBlocks.LURKING_LAMP, ModBlocks.OCTANT);

		HandledScreens.register(Astronomical.ASTRAL_DISPLAY_SCREEN_HANDLER, AstralDisplayScreen::new);
		HandledScreens.register(Astronomical.PLANET_COLOR_SCREEN_HANDLER, PlanetColorScreen::new);

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
	}

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
			float scale =(ringTexture == NanoRingItem.RingTexture.EYE_OF_THE_UNIVERSE ? 2f : 1f);

			matrixStack.scale(scale, scale,scale);
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
}
