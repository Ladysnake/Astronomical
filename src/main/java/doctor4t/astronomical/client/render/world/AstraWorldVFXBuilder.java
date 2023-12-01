package doctor4t.astronomical.client.render.world;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import com.sammy.lodestone.helpers.RenderHelper;
import com.sammy.lodestone.setup.LodestoneRenderLayers;
import com.sammy.lodestone.systems.rendering.Phases;
import com.sammy.lodestone.systems.rendering.VFXBuilders;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

import java.awt.*;

public class AstraWorldVFXBuilder extends VFXBuilders.WorldVFXBuilder {
	public static final LodestoneRenderLayers.RenderLayerProvider TEXTURE_ACTUAL_TRIANGLE = new LodestoneRenderLayers.RenderLayerProvider((texture) -> LodestoneRenderLayers.createGenericRenderLayer(texture.getNamespace(), "texture_actual_triangle", VertexFormats.POSITION_COLOR_TEXTURE, VertexFormat.DrawMode.TRIANGLES, RenderPhase.POSITION_COLOR_TEXTURE_LIGHTMAP_SHADER, Phases.NO_TRANSPARENCY, texture));
	public static final LodestoneRenderLayers.RenderLayerProvider TEXTURE_ACTUAL_TRIANGLE_ADDITIVE = new LodestoneRenderLayers.RenderLayerProvider((texture) -> LodestoneRenderLayers.createGenericRenderLayer(texture.getNamespace(), "texture_actual_triangle_additive", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT, VertexFormat.DrawMode.TRIANGLES, RenderPhase.POSITION_COLOR_TEXTURE_LIGHTMAP_SHADER, Phases.ADDITIVE_TRANSPARENCY, texture));
	public static final LodestoneRenderLayers.RenderLayerProvider TEXTURE_ACTUAL_TRIANGLE_TRANSPARENT = new LodestoneRenderLayers.RenderLayerProvider((texture) -> LodestoneRenderLayers.createGenericRenderLayer(texture.getNamespace(), "texture_actual_triangle_transparent", VertexFormats.POSITION_COLOR_TEXTURE, VertexFormat.DrawMode.TRIANGLES, RenderPhase.POSITION_COLOR_TEXTURE_LIGHTMAP_SHADER, Phases.NORMAL_TRANSPARENCY, texture));

	public AstraWorldVFXBuilder() {

	}


	@Override
	public VFXBuilders.WorldVFXBuilder renderQuad(VertexConsumer vertexConsumer, MatrixStack stack, Vec3f[] positions, float width, float height) {
		Matrix4f last = stack.peek().getModel();

        this.supplier.placeVertex(vertexConsumer, last, positions[0].getX()+xOffset, positions[0].getY()+yOffset, positions[0].getZ()+zOffset, this.u0, this.v1);
        this.supplier.placeVertex(vertexConsumer, last, positions[1].getX()+xOffset, positions[1].getY()+yOffset, positions[1].getZ()+zOffset, this.u1, this.v1);
        this.supplier.placeVertex(vertexConsumer, last, positions[2].getX()+xOffset, positions[2].getY()+yOffset, positions[2].getZ()+zOffset, this.u1, this.v0);
        this.supplier.placeVertex(vertexConsumer, last, positions[3].getX()+xOffset, positions[3].getY()+yOffset, positions[3].getZ()+zOffset, this.u0, this.v0);

		return this;
	}
	public VFXBuilders.WorldVFXBuilder renderQuad(VertexConsumer vertexConsumer, MatrixStack stack, VertexData data, Runnable r) {
		Matrix4f last = stack.peek().getModel();
		int[] iter = new int[1];
		setVertexSupplier((c, l, x, y, z, u, v) -> {
			Color col = data.color()[iter[0]];
			if (l == null)
				c.vertex(x, y, z).color(col.getRed()/255f, col.getGreen()/255f, col.getBlue()/255f, col.getAlpha()/255f*this.a).uv(u, v).light(this.light).next();
			else
				c.vertex(l, x, y, z).color(col.getRed()/255f, col.getGreen()/255f, col.getBlue()/255f, col.getAlpha()/255f*this.a).uv(u, v).light(this.light).next();
			iter[0]++;
		}).setFormat(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);

		this.supplier.placeVertex(vertexConsumer, last, data.vertices()[0].getX()+xOffset, data.vertices()[0].getY()+yOffset, data.vertices()[0].getZ()+zOffset, data.uv()[0].x, data.uv()[0].y);
		this.supplier.placeVertex(vertexConsumer, last, data.vertices()[1].getX()+xOffset, data.vertices()[1].getY()+yOffset, data.vertices()[1].getZ()+zOffset, data.uv()[1].x, data.uv()[1].y);
		this.supplier.placeVertex(vertexConsumer, last, data.vertices()[2].getX()+xOffset, data.vertices()[2].getY()+yOffset, data.vertices()[2].getZ()+zOffset, data.uv()[2].x, data.uv()[2].y);
		this.supplier.placeVertex(vertexConsumer, last, data.vertices()[3].getX()+xOffset, data.vertices()[3].getY()+yOffset, data.vertices()[3].getZ()+zOffset, data.uv()[3].x, data.uv()[3].y);

		r.run();

		return this;
	}
	@Override
	public VFXBuilders.WorldVFXBuilder renderSphere(VertexConsumer vertexConsumer, MatrixStack stack, float radius, int longs, int lats) {
		Matrix4f last = stack.peek().getModel();
		float startU = 0;
		float startV = 0;
		float endU = MathHelper.PI * 2;
		float endV = MathHelper.PI;
		float stepU = (endU - startU) / longs;
		float stepV = (endV - startV) / lats;
		for (int i = 0; i < longs; ++i) {
			// U-points
			for (int j = 0; j < lats; ++j) {
				// V-points
				float u = i * stepU + startU;
				float v = j * stepV + startV;
				float un = (i + 1 == longs) ? endU : (i + 1) * stepU + startU;
				float vn = (j + 1 == lats) ? endV : (j + 1) * stepV + startV;

				float p0x = MathHelper.cos(u) * MathHelper.sin(v) * radius;
				float p0y = MathHelper.cos(v) * radius;
				float p0z = MathHelper.sin(u) * MathHelper.sin(v) * radius;

				float p1x = MathHelper.cos(u) * MathHelper.sin(vn) * radius;
				float p1y = MathHelper.cos(vn) * radius;
				float p1z = MathHelper.sin(u) * MathHelper.sin(vn) * radius;

				float p2x = MathHelper.cos(un) * MathHelper.sin(v) * radius;
				float p2y = MathHelper.cos(v) * radius;
				float p2z = MathHelper.sin(un) * MathHelper.sin(v) * radius;

				float p3x = MathHelper.cos(un) * MathHelper.sin(vn) * radius;
				float p3y = MathHelper.cos(vn) * radius;
				float p3z = MathHelper.sin(un) * MathHelper.sin(vn) * radius;

				float textureU = u / endU * radius;
				float textureV = v / endV * radius;
				float textureUN = un / endU * radius;
				float textureVN = vn / endV * radius;

				RenderHelper.vertexPosColorUVLight(vertexConsumer, last, p0x, p0y, p0z, r, g, b, a, textureU, textureV, light);
				RenderHelper.vertexPosColorUVLight(vertexConsumer, last, p2x, p2y, p2z, r, g, b, a, textureUN, textureV, light);
				RenderHelper.vertexPosColorUVLight(vertexConsumer, last, p1x, p1y, p1z, r, g, b, a, textureU, textureVN, light);

				RenderHelper.vertexPosColorUVLight(vertexConsumer, last, p3x, p3y, p3z, r, g, b, a, textureUN, textureVN, light);
				RenderHelper.vertexPosColorUVLight(vertexConsumer, last, p1x, p1y, p1z, r, g, b, a, textureU, textureVN, light);
				RenderHelper.vertexPosColorUVLight(vertexConsumer, last, p2x, p2y, p2z, r, g, b, a, textureUN, textureV, light);
			}
		}
		return this;
	}
}
