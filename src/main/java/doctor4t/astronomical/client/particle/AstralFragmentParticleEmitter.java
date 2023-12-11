package doctor4t.astronomical.client.particle;

import com.sammy.lodestone.setup.LodestoneScreenParticles;
import com.sammy.lodestone.systems.rendering.particle.Easing;
import com.sammy.lodestone.systems.rendering.particle.ParticleBuilders;
import com.sammy.lodestone.systems.rendering.particle.screen.base.ScreenParticle;
import doctor4t.astronomical.common.Astronomical;
import net.minecraft.item.ItemStack;
import net.minecraft.util.random.RandomGenerator;

public class AstralFragmentParticleEmitter {
	public static void particleTick(ItemStack stack, float x, float y, ScreenParticle.RenderOrder renderOrder) {
		RandomGenerator random = RandomGenerator.createLegacy();
		if (random.nextInt(5) == 0) {
			ParticleBuilders.create(LodestoneScreenParticles.TWINKLE)
				.setScale(.1f + random.nextFloat() * .3f)
				.setColor(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE)
				.setAlpha(0, 1f, 0)
				.setAlphaEasing(Easing.QUAD_OUT, Easing.SINE_OUT)
				.enableNoClip()
				.setLifetime(20)
				.setSpinOffset(random.nextFloat() * 360f)
				.setSpin((float) (random.nextGaussian() / 20f))
				.overrideRenderOrder(renderOrder)
				.randomOffset(7)
				.spawn(x, y);
		}
	}
}
