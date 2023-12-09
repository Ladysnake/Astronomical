package doctor4t.astronomical.common.structure;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.util.Vec2d;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class Star implements CelestialObject {
	public static final Identifier TEMPTEX = Astronomical.id("textures/vfx/temp.png");
	private static final Identifier ID = Astronomical.id("star");
	protected Vec3d directionalVector;
	private float size, alpha;
	private int color;
	private int randomOffset;

	public Star() {
	}

	public Star(Vec3d vec, float size, float alpha, int color, int randomOffset) {
		this.directionalVector = vec;
		this.size = size;
		this.alpha = alpha;
		this.color = color;
		this.randomOffset = randomOffset;
	}

	public static Vec3d decompressDirectionalVector(double pitch, double yaw) {
		return new Vec3d(
			Math.sin(yaw) * Math.cos(pitch),
			Math.sin(pitch),
			Math.cos(pitch) * Math.cos(yaw));
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
	public float getAlpha() {
		return this.alpha;
	}

	@Override
	public int getColor() {
		return this.color;
	}

	@Override
	public int getRandomOffset() {
		return this.randomOffset;
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		size = nbt.getFloat("s");
		alpha = nbt.getFloat("a");
		color = nbt.getInt("c");
		randomOffset = nbt.getInt("o");
		this.directionalVector = new Vec3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		nbt.putFloat("s", size);
		nbt.putFloat("a", alpha);
		nbt.putFloat("c", color);
		nbt.putFloat("o", randomOffset);
		nbt.putDouble("x", directionalVector.x);
		nbt.putDouble("y", directionalVector.y);
		nbt.putDouble("z", directionalVector.z);
	}

	private Vec2d compressDirectionalVector() {
		return new Vec2d(Math.asin(directionalVector.y), Math.atan2(directionalVector.z, directionalVector.x));
	}
}
