package doctor4t.astronomical.cca.world;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import doctor4t.astronomical.common.structure.CelestialObject;
import doctor4t.astronomical.common.structure.Starfall;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

public class AstraStarfallComponent implements AutoSyncedComponent {
	private final List<Starfall> heavenlySpheres = new LinkedList<>();
	private final World obj;
	public AstraStarfallComponent(World object) {
		this.obj = object;
	}
	@Override
	public void readFromNbt(NbtCompound tag) {

	}

	@Override
	public void writeToNbt(NbtCompound tag) {

	}
}
