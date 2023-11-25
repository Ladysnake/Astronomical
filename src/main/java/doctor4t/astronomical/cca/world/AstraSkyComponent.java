package doctor4t.astronomical.cca.world;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import doctor4t.astronomical.client.AstraClientUtil;
import doctor4t.astronomical.common.structure.CelestialObject;
import doctor4t.astronomical.common.structure.Star;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

public class AstraSkyComponent implements AutoSyncedComponent {
	private final List<CelestialObject> heavenlySpheres = new LinkedList<>();
	private final RandomGenerator random;
	private final World obj;
	public AstraSkyComponent(World object) {
		this.obj = object;
		long seed = this.getWorldSeed(this.obj);
		this.random = RandomGenerator.createLegacy(seed);
	}

	private Vec3d generateDirectionalVector() {
		double x = this.random.nextGaussian();
		double y = this.random.nextGaussian();
		double z = this.random.nextGaussian();
		return new Vec3d(x, y, z).normalize();
	}

	private long getWorldSeed(World w) {
		//not sure how to do this so
		return 666L;
	}
	@Override
	public void readFromNbt(NbtCompound tag) {
		for(int i = 0; i < 9000; i++) {
            this.heavenlySpheres.add(new Star(this.generateDirectionalVector(), 1f+0.7f* this.random.nextFloat()));
		}
	}

	public List<CelestialObject> getCelestialObjects() {
		return this.heavenlySpheres;
	}

	@Override
	public void writeToNbt(NbtCompound tag) {

	}
}
