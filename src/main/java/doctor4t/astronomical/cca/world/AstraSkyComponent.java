package doctor4t.astronomical.cca.world;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import doctor4t.astronomical.client.render.world.AstraSkyRenderer;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.init.AstraCelestialObjects;
import doctor4t.astronomical.common.structure.CelestialObject;
import doctor4t.astronomical.common.structure.InteractableStar;
import doctor4t.astronomical.common.structure.Star;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * NEVER SYNC THIS
 * </p>
 * thanks
 *
 **/
public class AstraSkyComponent implements AutoSyncedComponent {
	private static Identifier def = Astronomical.id("star");
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
		NbtList nbtList = tag.getList("spherical", 10);
		heavenlySpheres.clear();
		nbtList.forEach(dNbt -> {
			NbtCompound n = (NbtCompound) dNbt;
			CelestialObject star = getObjectByType(n);
			star.readNbt(n);
			heavenlySpheres.add(star);
		});
		if(heavenlySpheres.isEmpty()) {
			for (int i = 0; i < 2000; i++) {
				this.heavenlySpheres.add(new Star(this.generateDirectionalVector(), 0.7f + 0.5f * this.random.nextFloat(), this.random.nextFloat()));
			}
			for (int i = 0; i < 10; i++) {
				this.heavenlySpheres.add(new InteractableStar(this.generateDirectionalVector(), 2f + 0.6f * this.random.nextFloat(), 0.9f + 0.1f * this.random.nextFloat()));
			}
		}
		if(obj != null && obj.isClient())
			RenderSystem.recordRenderCall(AstraSkyRenderer::redrawStars);
	}

	public List<CelestialObject> getCelestialObjects() {
		return this.heavenlySpheres;
	}
	public CelestialObject getObjectByType(NbtCompound nbt) {
		if(nbt.contains("id")) {
			return AstraCelestialObjects.OBJECTS.get(new Identifier(nbt.getString("id"))).get();
		} else {
			return AstraCelestialObjects.OBJECTS.get(def).get();
		}
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		NbtList list = new NbtList();

		heavenlySpheres.forEach(s -> {
			NbtCompound dNbt = new NbtCompound();

			s.writeNbt(dNbt);

			list.add(dNbt);
		});
		tag.put("spherical", list);

	}
}
