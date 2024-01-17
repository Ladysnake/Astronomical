package doctor4t.astronomical.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VertexConsumer.class)
public interface VertexConsumerMixin {
//	@Shadow
//	VertexConsumer vertex(double x, double y, double z);
//
//	/**
//	 * @author
//	 * @reason
//	 */
//	@Overwrite
//	default VertexConsumer vertex(Matrix4f matrix, float x, float y, float z) {
//		float f = x;
//		float g = y;
//		float h = z;
//		x = matrix.m00() * f + matrix.m01() * g + matrix.m02() * h + matrix.m03();
//		y = matrix.m10() * f + matrix.m11() * g + matrix.m12() * h + matrix.m13();
//		z = matrix.m20() * f + matrix.m21() * g + matrix.m22() * h + matrix.m23();
//
//		return this.vertex(x, y, z);
//	}
}
