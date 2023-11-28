package doctor4t.astronomical.client;

import doctor4t.astronomical.client.render.entity.StarEntityRenderer;
import doctor4t.astronomical.client.render.entity.block.AstralDisplayBlockEntityRenderer;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.block.entity.AstralDisplayBlockEntity;
import doctor4t.astronomical.common.init.ModBlockEntities;
import doctor4t.astronomical.common.init.ModEntities;
import doctor4t.astronomical.common.init.ModItems;
import doctor4t.astronomical.common.init.ModParticles;
import doctor4t.astronomical.common.item.MarshmallowStickItem;
import doctor4t.astronomical.common.screen.AstralDisplayScreen;
import doctor4t.astronomical.common.screen.AstralDisplayScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

import java.util.HashMap;

public class AstronomicalClient implements ClientModInitializer {
	//https://www.youtube.com/watch?v=phWWx4NRhpE
	public static final HashMap<BlockPos, Vec3d> ORBITING_POSITIONS = new HashMap<>();

	@Override
	public void onInitializeClient(ModContainer mod) {
		// init model layers
//		ModModelLayers.initialize();

		// entity renderers registration
		BlockEntityRendererFactories.register(ModBlockEntities.ASTRAL_DISPLAY, AstralDisplayBlockEntityRenderer::new);
		EntityRendererRegistry.register(ModEntities.STAR, StarEntityRenderer::new);

		// particle renderers registration
		ModParticles.registerFactories();

		// block special layers
//		BlockRenderLayerMap.put(RenderLayer.getCutout(), ModBlocks.LOCKER, ModBlocks.CLOSED_LOCKER, ModBlocks.WALKWAY, ModBlocks.WALKWAY_STAIRS, ModBlocks.ABYSSTEEL_CHAIN, ModBlocks.ANGLERWEED, ModBlocks.ANGLERWEED_PLANT, ModBlocks.LURKING_LAMP, ModBlocks.OCTANT);

		HandledScreens.register(Astronomical.ASTRAL_DISPLAY_SCREEN_HANDLER, AstralDisplayScreen::new);

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
						display.yLevel = yLevel;
						display.rotSpeed = rotSpeed;
						display.spin = spin;
						if (minecraftClient.currentScreen instanceof AstralDisplayScreen screen) {
							screen.addSliders();
						}
					}
				}
			});
		});

		ModelPredicateProviderRegistry.register(ModItems.MARSHMALLOW_STICK, Astronomical.id("marshmallow"), (stack, world, entity, seed) -> MarshmallowStickItem.CookState.getCookState(stack).ordinal() / (float) MarshmallowStickItem.CookState.values().length);
	}
}
