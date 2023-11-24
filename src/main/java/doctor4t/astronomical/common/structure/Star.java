package doctor4t.astronomical.common.structure;

import doctor4t.astronomical.common.Astronomical;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class Star implements CelestialObject {
	public static final Identifier TEMPTEX = Astronomical.id("textures/vfx/temp.png");
	protected Vec3d directionalVector;
	private float size;
	public Star(Vec3d vec, float size) {
		this.directionalVector = vec;
		this.size = size;
	}

	@Override
	public Vec3d getDirectionVector() {
		return directionalVector;
	}

	@Override
	public float getSize() {
		return size;
	}

	@Override
	public Identifier getTexture() {
		return TEMPTEX;
	}
}
