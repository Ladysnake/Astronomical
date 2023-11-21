package doctor4t.astronomical.common.structure;

import doctor4t.astronomical.common.Astronomical;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class Star implements CelestialObject {
	private static final Identifier TEMPTEX = Astronomical.id("textures/vfx/temp.png");
	protected Vec3d directionalVector;
	public Star(Vec3d vec) {
		this.directionalVector = vec;
	}

	@Override
	public Vec3d getDirectionVector() {
		return directionalVector;
	}

	@Override
	public Identifier getTexture() {
		return TEMPTEX;
	}
}
