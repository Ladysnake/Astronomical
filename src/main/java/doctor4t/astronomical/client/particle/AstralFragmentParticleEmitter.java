package doctor4t.astronomical.client.particle;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.init.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;
import team.lodestar.lodestone.setup.LodestoneScreenParticles;
import team.lodestar.lodestone.systems.rendering.particle.Easing;
import team.lodestar.lodestone.systems.rendering.particle.ScreenParticleBuilder;
import team.lodestar.lodestone.systems.rendering.particle.data.ColorParticleData;
import team.lodestar.lodestone.systems.rendering.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.rendering.particle.data.SpinParticleData;
import team.lodestar.lodestone.systems.rendering.particle.screen.LodestoneScreenParticleTextureSheet;
import team.lodestar.lodestone.systems.rendering.particle.screen.base.ScreenParticle;

import java.util.ArrayList;
import java.util.HashMap;

public class AstralFragmentParticleEmitter {
	public static void particleTick(HashMap<LodestoneScreenParticleTextureSheet, ArrayList<ScreenParticle>> target, World world, float tickDelta, ItemStack stack, float x, float y) {
		if (stack == ModItems.CREATIVE_TAB_ASTRAL_FRAGMENT) {
			//todo fix the weird offset
			return;
		}
		RandomGenerator random = RandomGenerator.createLegacy();
		if (random.nextInt(5) == 0) {
			ScreenParticleBuilder.create(LodestoneScreenParticles.TWINKLE, target)
					.setScaleData(GenericParticleData.create(.1f + random.nextFloat() * .3f).build())
					.setColorData(ColorParticleData.create(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE).build())
					.setTransparencyData(GenericParticleData.create(0, 1f, 0).setEasing(Easing.QUAD_OUT, Easing.SINE_OUT).build())
					.setLifetime(20)
					.setSpinData(SpinParticleData.create((float) (random.nextGaussian() / 20f)).setSpinOffset(random.nextFloat() * 360f).build())
					.setRandomOffset(7)
					.spawn(x, y);
		}
	}
}
