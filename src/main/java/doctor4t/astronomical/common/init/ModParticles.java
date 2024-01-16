package doctor4t.astronomical.common.init;

import doctor4t.astronomical.client.particle.ExplosionParticleType;
import doctor4t.astronomical.common.Astronomical;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public interface ModParticles {
	ExplosionParticleType STAR_IMPACT_EXPLOSION = new ExplosionParticleType();
	ExplosionParticleType STAR_IMPACT_FLARE = new ExplosionParticleType();
	ExplosionParticleType FALLEN_STAR = new ExplosionParticleType();

	static void initialize() {
		initParticles(bind(Registries.PARTICLE_TYPE));
	}

	static void registerFactories() {
		ParticleFactoryRegistry.getInstance().register(STAR_IMPACT_EXPLOSION, ExplosionParticleType.Factory::new);
		ParticleFactoryRegistry.getInstance().register(STAR_IMPACT_FLARE, ExplosionParticleType.Factory::new);
		ParticleFactoryRegistry.getInstance().register(FALLEN_STAR, ExplosionParticleType.Factory::new);
	}

	private static void initParticles(BiConsumer<ParticleType<?>, Identifier> registry) {
		registry.accept(STAR_IMPACT_EXPLOSION, new Identifier(Astronomical.MOD_ID, "star_impact_explosion"));
		registry.accept(STAR_IMPACT_FLARE, new Identifier(Astronomical.MOD_ID, "star_impact_flare"));
		registry.accept(FALLEN_STAR, new Identifier(Astronomical.MOD_ID, "fallen_star"));
	}

	private static <T> BiConsumer<T, Identifier> bind(Registry<? super T> registry) {
		return (t, id) -> Registry.register(registry, id, t);
	}
}
