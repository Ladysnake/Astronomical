package doctor4t.astronomical.client.particle;

import com.mojang.serialization.Codec;
import com.sammy.lodestone.systems.rendering.particle.world.WorldParticleEffect;
import net.fabricmc.fabric.impl.client.particle.FabricSpriteProviderImpl;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleType;

public class OrbParticleType extends ParticleType<WorldParticleEffect> {
	public OrbParticleType() {
		super(false, WorldParticleEffect.DESERIALIZER);
	}

	@Override
	public boolean shouldAlwaysSpawn() {
		return true;
	}

	@Override
	public Codec<WorldParticleEffect> getCodec() {
		return WorldParticleEffect.codecFor(this);
	}

	public static class Factory implements ParticleFactory<WorldParticleEffect> {
		private final SpriteProvider sprite;

		public Factory(SpriteProvider sprite) {
			this.sprite = sprite;
		}

		@Override
		public Particle createParticle(WorldParticleEffect data, ClientWorld world, double x, double y, double z, double mx, double my, double mz) {
			return new OrbParticle(world, data, (FabricSpriteProviderImpl) sprite, x, y, z, mx, my, mz);
		}
	}
}
