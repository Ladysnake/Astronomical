package doctor4t.astronomical.cca.world;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import doctor4t.astronomical.common.structure.Starfall;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

public class AstraStarfallComponent implements AutoSyncedComponent, ServerTickingComponent {
	private final List<Starfall> starfalls = new LinkedList<>();
	private final World obj;
	public AstraStarfallComponent(World object) {
		this.obj = object;
	}
	@Override
	public void readFromNbt(NbtCompound tag) {
		NbtList nbtList = tag.getList("falls", 10);
		starfalls.clear();
		nbtList.forEach(dNbt -> {
			NbtCompound n = (NbtCompound) dNbt;
			Starfall s = new Starfall(n);
			starfalls.add(s);
		});
	}
	public void addFall(Vec3d rot, Vec3d target) {
		starfalls.add(new Starfall(rot, target));
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		NbtList list = new NbtList();

		starfalls.forEach(s -> {
			NbtCompound dNbt = new NbtCompound();
			s.writeNbt(dNbt);
			list.add(dNbt);
		});
		tag.put("falls", list);
	}

	@Override
	public void serverTick() {
		starfalls.forEach(s -> s.tick(obj));
		starfalls.removeIf(s -> s.progress >= 400);
	}
}
