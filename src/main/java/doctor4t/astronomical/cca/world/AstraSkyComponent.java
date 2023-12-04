package doctor4t.astronomical.cca.world;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import doctor4t.astronomical.cca.AstraCardinalComponents;
import doctor4t.astronomical.client.render.world.AstraSkyRenderer;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.init.AstraCelestialObjects;
import doctor4t.astronomical.common.structure.CelestialObject;
import doctor4t.astronomical.common.structure.InteractableStar;
import doctor4t.astronomical.common.structure.Star;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
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
public class AstraSkyComponent implements AutoSyncedComponent, ServerTickingComponent {
	private static final Identifier def = Astronomical.id("star");
	private final List<CelestialObject> heavenlySpheres = new LinkedList<>();
	private static final int maximumStars = 10;
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

	@Override
	public void serverTick() {
		if(obj.getTime() % 4000 == 0 && obj.random.nextFloat() > 0.4f) {
			regenerate();
		}
	}

	public void regenerate() {
		if(heavenlySpheres.size() < maximumStars) {
			this.heavenlySpheres.add(generateStar());
			AstraCardinalComponents.SKY.sync(obj);
		}
	}

	private CelestialObject generateStar() {
		Vec3d rot = generateDirectionalVector();
		return new InteractableStar(rot.normalize(), 3f, 1);
	}
}
