package doctor4t.astronomical.common.block.entity;

import com.sammy.lodestone.systems.rendering.particle.ParticleBuilders;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.init.ModBlockEntities;
import doctor4t.astronomical.common.init.ModParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AstralLanternBlockEntity extends BlockEntity {
	public AstralLanternBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.ASTRAL_LANTERN, pos, state);
	}

	public static <T extends BlockEntity> void tick(World world, BlockPos blockPos, BlockState blockState, T t) {
		Vec3d blockPosMiddle = Vec3d.ofCenter(t.getPos());
		float scale = .3f;

		ParticleBuilders.create(ModParticles.FALLEN_STAR)
			.setScale((.8f + world.random.nextFloat() / 3f)*scale)
			.setColor(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE)
			.setAlpha(0, 0.3f, 0)
			.enableNoClip()
			.setLifetime(20)
			.setSpinOffset(world.random.nextFloat() * 360f)
			.setSpin((float) (world.random.nextGaussian() / 100f))
			.spawn(world, blockPosMiddle.getX(), blockPosMiddle.getY(), blockPosMiddle.getZ());

		ParticleBuilders.create(ModParticles.STAR_IMPACT_FLARE)
			.setScale((6f + world.random.nextFloat() * 5f)*scale)
			.setColor(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE)
			.setAlpha(0, 0.2f, 0)
			.enableNoClip()
			.setLifetime(20)
			.setSpinOffset(world.random.nextFloat() * 360f)
			.setSpin((float) (world.random.nextGaussian() / 100f))
			.spawn(world, blockPosMiddle.getX(), blockPosMiddle.getY(), blockPosMiddle.getZ());

		ParticleBuilders.create(ModParticles.FALLEN_STAR)
			.randomOffset((world.random.nextFloat() * Math.signum(world.random.nextGaussian()) *10f)*scale)
			.setScale((.2f + world.random.nextFloat() / 10f)*scale)
			.setColor(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE)
			.setAlpha(0, .5f, 0)
			.enableNoClip()
			.setLifetime(20 + world.random.nextInt(20))
			.setSpinOffset(world.random.nextFloat() * 360f)
			.setSpin((float) (world.random.nextGaussian() / 50f))
			.spawn(world, blockPosMiddle.getX(), blockPosMiddle.getY(), blockPosMiddle.getZ());
	}
}
