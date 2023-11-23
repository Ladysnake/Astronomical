package doctor4t.astronomical.client.render.world;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import com.sammy.lodestone.helpers.RenderHelper;
import com.sammy.lodestone.setup.LodestoneRenderLayers;
import com.sammy.lodestone.setup.LodestoneShaders;
import com.sammy.lodestone.systems.rendering.Phases;
import com.sammy.lodestone.systems.rendering.VFXBuilders;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class AstraWorldVFXBuilder extends VFXBuilders.WorldVFXBuilder {
	public static final LodestoneRenderLayers.RenderLayerProvider ADDITIVE_TEXTURE_ACTUAL_TRIANGLE = new LodestoneRenderLayers.RenderLayerProvider((texture) -> LodestoneRenderLayers.createGenericRenderLayer(texture.getNamespace(), "additive_texture_actual_triangle", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT, VertexFormat.DrawMode.TRIANGLES, RenderPhase.POSITION_COLOR_TEXTURE_LIGHTMAP_SHADER, Phases.NO_TRANSPARENCY, texture));

	float u0 = 0, v0 = 0, u1 = 1, v1 = 1;
	WorldVertexPlacementSupplier supplier;
	public AstraWorldVFXBuilder() {

	}

	@Override
	public VFXBuilders.WorldVFXBuilder setVertexSupplier(WorldVertexPlacementSupplier supplier) {
		this.supplier = supplier;
		return super.setVertexSupplier(supplier);
	}

	@Override
	public VFXBuilders.WorldVFXBuilder setUV(float u0, float v0, float u1, float v1) {
		this.u0 = u0;
		this.v0 = v0;
		this.u1 = u1;
		this.v1 = v1;
		return super.setUV(u0, v0, u1, v1);
	}

	@Override
	public VFXBuilders.WorldVFXBuilder renderQuad(VertexConsumer vertexConsumer, MatrixStack stack, Vec3f[] positions, float width, float height) {
		Matrix4f last = stack.peek().getModel();

		supplier.placeVertex(vertexConsumer, last, positions[0].getX(), positions[0].getY(), positions[0].getZ(), u0, v1);
		supplier.placeVertex(vertexConsumer, last, positions[1].getX(), positions[1].getY(), positions[1].getZ(), u1, v1);
		supplier.placeVertex(vertexConsumer, last, positions[2].getX(), positions[2].getY(), positions[2].getZ(), u1, v0);
		supplier.placeVertex(vertexConsumer, last, positions[3].getX(), positions[3].getY(), positions[3].getZ(), u0, v0);

		return this;
	}
}
