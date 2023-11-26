package doctor4t.astronomical.common.structure;

import net.minecraft.util.math.Vec3d;

public class Starfall {
	public final Vec3d startDirection, endPos;
	public int progress = 0;

	public Starfall(Vec3d startDirection, Vec3d end) {
		this.startDirection = startDirection;
		this.endPos = end;
	}
}
