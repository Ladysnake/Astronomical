package doctor4t.astronomical.client.render.entity.block;

import com.sammy.lodestone.systems.rendering.particle.ParticleBuilders;
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
				ParticleBuilders.create(ModParticles.FALLEN_STAR)
					.setScale((.8f + world.random.nextFloat() / 3f) * scale)
					.setColor(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE)
					.setAlpha(0, 0.3f, 0)
					.enableNoClip()
					.setLifetime(20)
					.setSpinOffset(world.random.nextFloat() * 360f)
					.setSpin((float) (world.random.nextGaussian() / 100f))
					.spawn(world, blockPosMiddle.getX(), blockPosMiddle.getY(), blockPosMiddle.getZ());

				ParticleBuilders.create(ModParticles.STAR_IMPACT_FLARE)
					.setScale((6f + world.random.nextFloat() * 5f) * scale)
					.setColor(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE)
					.setAlpha(0, 0.2f, 0)
					.enableNoClip()
					.setLifetime(20)
					.setSpinOffset(world.random.nextFloat() * 360f)
					.setSpin((float) (world.random.nextGaussian() / 100f))
					.spawn(world, blockPosMiddle.getX(), blockPosMiddle.getY(), blockPosMiddle.getZ());

				ParticleBuilders.create(ModParticles.FALLEN_STAR)
					.randomOffset((world.random.nextFloat() * Math.signum(world.random.nextGaussian()) * 10f) * scale)
					.setScale((.2f + world.random.nextFloat() / 10f) * scale)
					.setColor(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE)
					.setAlpha(0, .5f, 0)
					.enableNoClip()
					.setLifetime(20 + world.random.nextInt(20))
					.setSpinOffset(world.random.nextFloat() * 360f)
					.setSpin((float) (world.random.nextGaussian() / 50f))
					.spawn(world, blockPosMiddle.getX(), blockPosMiddle.getY(), blockPosMiddle.getZ());
			}
		}
	}
}
