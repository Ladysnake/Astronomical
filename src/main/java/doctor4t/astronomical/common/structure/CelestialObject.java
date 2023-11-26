package doctor4t.astronomical.common.structure;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public interface CelestialObject {
	Vec3d getDirectionVector();
	float getSize();
	float getHeat();
	void readNbt(NbtCompound nbt);
	void writeNbt(NbtCompound nbt);
}
