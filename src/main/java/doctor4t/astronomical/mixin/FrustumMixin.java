package doctor4t.astronomical.mixin;

import doctor4t.astronomical.client.render.world.AstraFrustum;
import net.minecraft.client.render.Frustum;
import net.minecraft.util.math.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Frustum.class)
public class FrustumMixin implements AstraFrustum {
	@Shadow
	private double x;

	@Shadow
	private double y;

	@Shadow
	private double z;

	@Shadow
	@Final
	private Vector4f[] homogeneousCoordinates;

	@Override
	public boolean astra$isVisible(float x, float y, float z) {
//		x += (float) this.x;
//		y += (float) this.y;
//		z += (float) this.z;
		return this.astra$isPositionVisible(x, y, z);
	}

	@Override
	public boolean astra$isPositionVisible(float x1, float y1, float z1) {
		for(int i = 0; i < 6; ++i) {
			Vector4f vector4f = this.homogeneousCoordinates[i];
			if (!(vector4f.dotProduct(new Vector4f(x1, y1, z1, 1.0F)) > 0.0F)) {
				return false;
			}
		}

		return true;
	}
}
