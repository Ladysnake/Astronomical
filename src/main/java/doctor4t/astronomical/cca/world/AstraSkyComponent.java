package doctor4t.astronomical.cca.world;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import doctor4t.astronomical.cca.AstraCardinalComponents;
import doctor4t.astronomical.client.render.world.AstraSkyRenderer;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.init.AstraCelestialObjects;
import doctor4t.astronomical.common.structure.CelestialObject;
import doctor4t.astronomical.common.structure.InteractableStar;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

public class AstraSkyComponent implements AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent {
	private static final Identifier def = Astronomical.id("star");
	private static final int maximumStars = 5;
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
		return 666L + obj.getTime() % 1000 >> 7;
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
		if (obj != null && obj.isClient())
			RenderSystem.recordRenderCall(AstraSkyRenderer::redrawStars);
	}

	public List<CelestialObject> getCelestialObjects() {
		return this.heavenlySpheres;
	}

	public CelestialObject getObjectByType(NbtCompound nbt) {
		if (nbt.contains("id")) {
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
		if (obj.getTime() % 40 == 0 && obj.random.nextFloat() > 0.4f) {
			regenerate();
		}
		interactStarTick();
		boolean[] beel = new boolean[1];
		heavenlySpheres.stream().iterator().forEachRemaining(celestialObject -> {
			if (celestialObject instanceof InteractableStar s && s.supernovaTicks == InteractableStar.STARFALL_TICKS) {
				if (((InteractableStar) celestialObject).crossFire != null)
					((InteractableStar) celestialObject).crossFire.accept(obj);
				beel[0] = true;
			}
		});
		heavenlySpheres.removeIf(b -> b instanceof InteractableStar s && s.supernovaTicks >= InteractableStar.EXPLOSION_TICKS);
		if (beel[0]) {
			AstraCardinalComponents.SKY.sync(obj);
		}
	}

	public void regenerate() {
		if (heavenlySpheres.size() < maximumStars) {
			this.heavenlySpheres.add(generateStar());
			AstraCardinalComponents.SKY.sync(obj);
		}
	}

	private CelestialObject generateStar() {
		Vec3d up = new Vec3d(0, -1, 0);
		Vec3d rot = generateDirectionalVector();
		while (rot.dotProduct(up) < 0) {
			rot = generateDirectionalVector();
		}
		return new InteractableStar(rot.normalize(), 1.5f, .3f, Astronomical.getStarColorForTemperature(Astronomical.getRandomStarTemperature(this.random)), 1 + random.nextInt(10));
	}

	@Override
	public void clientTick() {
		interactStarTick();
	}

	private void interactStarTick() {
		for (CelestialObject c : heavenlySpheres) {
			if (c instanceof InteractableStar s && s.subjectForTermination) {
				s.supernovaTicks++;
			}
		}
	}
}
