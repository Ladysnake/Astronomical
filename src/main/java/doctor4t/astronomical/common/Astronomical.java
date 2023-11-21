package doctor4t.astronomical.common;

import doctor4t.astronomical.common.init.*;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class Astronomical implements ModInitializer {
	public static final String MOD_ID = "astronomical";

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

	@Override
	public void onInitialize(ModContainer mod) {
		ModBlocks.initialize();
		ModBlockEntities.initialize();
		ModEntities.initialize();
		ModItems.initialize();
		ModSoundEvents.initialize();
		ModParticles.initialize();
	}
}
