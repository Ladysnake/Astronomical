package doctor4t.astronomical.client.render.entity.block;

import doctor4t.astronomical.client.AstronomicalClient;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.block.entity.AstralLanternBlockEntity;
import doctor4t.astronomical.common.init.ModParticles;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import team.lodestar.lodestone.systems.rendering.particle.WorldParticleBuilder;
import team.lodestar.lodestone.systems.rendering.particle.data.ColorParticleData;
import team.lodestar.lodestone.systems.rendering.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.rendering.particle.data.SpinParticleData;

public class AstralLanternBlockEntityRenderer<T extends AstralLanternBlockEntity> implements BlockEntityRenderer<T> {
	public AstralLanternBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
	}

	@Override
	public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if (AstronomicalClient.renderStarsThisTick) {
			Vec3d blockPosMiddle = Vec3d.ofCenter(entity.getPos());
			float scale = .3f;
			World world = entity.getWorld();

			if (world != null) {
				WorldParticleBuilder.create(ModParticles.FALLEN_STAR)
						.setScaleData(GenericParticleData.create((.8f + world.random.nextFloat() / 3f) * scale).build())
						.setColorData(ColorParticleData.create(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE).build())
						.setTransparencyData(GenericParticleData.create(0, 0.3f, 0).build())
						.enableNoClip()
						.setLifetime(20)
						.setSpinData(SpinParticleData.create((float) (world.random.nextGaussian() / 100f)).setSpinOffset(world.random.nextFloat() * 360f).build())
						.spawn(world, blockPosMiddle.getX(), blockPosMiddle.getY(), blockPosMiddle.getZ());

				WorldParticleBuilder.create(ModParticles.STAR_IMPACT_FLARE)
						.setScaleData(GenericParticleData.create((6f + world.random.nextFloat() * 5f) * scale).build())
						.setColorData(ColorParticleData.create(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE).build())
						.setTransparencyData(GenericParticleData.create(0, 0.2f, 0).build())
						.enableNoClip()
						.setLifetime(20)
						.setSpinData(SpinParticleData.create((float) (world.random.nextGaussian() / 100f)).setSpinOffset(world.random.nextFloat() * 360f).build())
						.spawn(world, blockPosMiddle.getX(), blockPosMiddle.getY(), blockPosMiddle.getZ());

				WorldParticleBuilder.create(ModParticles.FALLEN_STAR)
						.setScaleData(GenericParticleData.create((.2f + world.random.nextFloat() / 10f) * scale).build())
						.setColorData(ColorParticleData.create(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE).build())
						.setTransparencyData(GenericParticleData.create(0, .5f, 0).build())
						.enableNoClip()
						.setLifetime(20 + world.random.nextInt(20))
						.setSpinData(SpinParticleData.create((float) (world.random.nextGaussian() / 50f)).setSpinOffset(world.random.nextFloat() * 360f).build())
						.setRandomOffset((world.random.nextFloat() * Math.signum(world.random.nextGaussian()) * 10f) * scale)
						.spawn(world, blockPosMiddle.getX(), blockPosMiddle.getY(), blockPosMiddle.getZ());
			}
		}
	}
}
