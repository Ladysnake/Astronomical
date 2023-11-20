package doctor4t.astronomical.client;

import doctor4t.astronomical.client.render.entity.StarEntityRenderer;
import doctor4t.astronomical.common.init.ModEntities;
import doctor4t.astronomical.common.init.ModParticles;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class AstronomicalClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		// init model layers
//		ModModelLayers.initialize();

		// entity renderers registration
		EntityRendererRegistry.register(ModEntities.STAR, StarEntityRenderer::new);

		// particle renderers registration
		ModParticles.registerFactories();

		// block special layers
//		BlockRenderLayerMap.put(RenderLayer.getCutout(), ModBlocks.LOCKER, ModBlocks.CLOSED_LOCKER, ModBlocks.WALKWAY, ModBlocks.WALKWAY_STAIRS, ModBlocks.ABYSSTEEL_CHAIN, ModBlocks.ANGLERWEED, ModBlocks.ANGLERWEED_PLANT, ModBlocks.LURKING_LAMP, ModBlocks.OCTANT);
	}

}
