package doctor4t.astronomical.common.init;

import doctor4t.astronomical.client.particle.OrbParticleType;
import doctor4t.astronomical.common.Astronomical;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.BiConsumer;

public interface ModParticles {
	OrbParticleType ORB = new OrbParticleType();

	static void initialize() {
		initParticles(bind(Registry.PARTICLE_TYPE));
	}

	static void registerFactories() {
		ParticleFactoryRegistry.getInstance().register(ORB, OrbParticleType.Factory::new);
	}

	private static void initParticles(BiConsumer<ParticleType<?>, Identifier> registry) {
		registry.accept(ORB, new Identifier(Astronomical.MOD_ID, "orb"));
	}

	private static <T> BiConsumer<T, Identifier> bind(Registry<? super T> registry) {
		return (t, id) -> Registry.register(registry, id, t);
	}
}
