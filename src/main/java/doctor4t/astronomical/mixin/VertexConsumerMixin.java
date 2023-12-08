package doctor4t.astronomical.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VertexConsumer.class)
public interface VertexConsumerMixin {
	@Shadow
	VertexConsumer vertex(double x, double y, double z);

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	default VertexConsumer vertex(Matrix4f matrix, float x, float y, float z) {
		float f = x;
		float g = y;
		float h = z;
		x = matrix.a00 * f + matrix.a01 * g + matrix.a02 * h + matrix.a03;
		y = matrix.a10 * f + matrix.a11 * g + matrix.a12 * h + matrix.a13;
		z = matrix.a20 * f + matrix.a21 * g + matrix.a22 * h + matrix.a23;

		return this.vertex(x, y, z);
	}
}
