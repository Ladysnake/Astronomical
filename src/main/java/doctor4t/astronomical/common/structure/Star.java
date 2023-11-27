package doctor4t.astronomical.common.structure;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.util.Vec2d;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Star implements CelestialObject {
	private static final Identifier ID = Astronomical.id("star");
	public static final Identifier TEMPTEX = Astronomical.id("textures/vfx/temp.png");
	protected Vec3d directionalVector;
	private float size, heat;
	public Star() {}
	public Star(Vec3d vec, float size, float heat) {
		this.directionalVector = vec;
		this.size = size;
		this.heat = heat;
	}

	@Override
	public boolean canInteract() {
		return false;
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public Vec3d getDirectionVector() {
		return this.directionalVector;
	}

	@Override
	public float getSize() {
		return this.size;
	}

	@Override
	public float getHeat() {
		return heat;
	}


	@Override
	public void readNbt(NbtCompound nbt) {
		size = nbt.getFloat("s");
		heat = nbt.getFloat("h");
		this.directionalVector = new Vec3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
	}


	@Override
	public void writeNbt(NbtCompound nbt) {
		nbt.putFloat("s", size);
		nbt.putFloat("h", heat);
		nbt.putDouble("x", directionalVector.x);
		nbt.putDouble("y", directionalVector.y);
		nbt.putDouble("z", directionalVector.z);
	}

	private Vec2d compressDirectionalVector() {
		return new Vec2d(Math.asin(directionalVector.y), Math.atan2(directionalVector.z, directionalVector.x));
	}
	public static Vec3d decompressDirectionalVector(double pitch, double yaw) {
		return new Vec3d(
			Math.sin(yaw)*Math.cos(pitch),
			Math.sin(pitch),
			Math.cos(pitch)*Math.cos(yaw));
	}
}
