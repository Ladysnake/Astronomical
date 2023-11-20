package doctor4t.astronomical.common.structure;

import com.sammy.lodestone.systems.rendering.particle.ParticleBuilders;
import doctor4t.astronomical.common.init.ModParticles;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
/**
 * Test star class.
 **/
public class TestStar extends Star {
	public final Vec3d position;
	public TestStar(Vec3d position, Color start, Color end) {
		super(start, end);
		this.position = position;
	}

	@Override
	public Vec3d getPos() {
		return position;
	}

	@Override
	public void emit(Vec3d position, World world, ClientPlayerEntity view) {
		boolean isWitnessed = view.getRotationVector().dotProduct(position.subtract(view.getCameraPosVec(1)).normalize()) > 0.999f;
		ParticleBuilders.create(ModParticles.ORB)
			.setColor(isWitnessed ? start : end, end)
			.setScale((10f + world.random.nextFloat() / 10f))
			.setAlpha(0, 1f, 0)
			.enableNoClip()
			.setLifetime(20)
			.setSpin(-world.random.nextFloat() / 7f)
			.spawn(world, position.x, position.y, position.z);
	}
}
