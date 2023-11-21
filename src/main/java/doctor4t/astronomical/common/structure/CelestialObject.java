package doctor4t.astronomical.common.structure;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public interface CelestialObject {
	Vec3d getDirectionVector();
	Identifier getTexture();
}
