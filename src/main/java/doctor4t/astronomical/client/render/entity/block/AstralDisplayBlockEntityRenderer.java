package doctor4t.astronomical.client.render.entity.block;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.sammy.lodestone.setup.LodestoneParticles;
import com.sammy.lodestone.setup.LodestoneRenderLayers;
import com.sammy.lodestone.setup.LodestoneShaders;
import com.sammy.lodestone.systems.rendering.Phases;
import com.sammy.lodestone.systems.rendering.VFXBuilders;
import com.sammy.lodestone.systems.rendering.particle.ParticleBuilders;
import doctor4t.astronomical.client.render.world.AstraWorldVFXBuilder;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.block.entity.AstralDisplayBlockEntity;
import doctor4t.astronomical.common.init.ModParticles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.random.RandomGenerator;

import java.awt.*;

public class AstralDisplayBlockEntityRenderer<T extends AstralDisplayBlockEntity> implements BlockEntityRenderer<T> {
	public AstralDisplayBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
	}

	public void render(T entity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		if (entity.getWorld() != null) {
			float time = ((float) (MinecraftClient.getInstance().world.getTime() % 2400000L) + MinecraftClient.getInstance().getTickDelta());

			matrixStack.push();
			matrixStack.translate(.5f, .5f,.5f);


			float speedModifier = 0.01f;
			float value = time;
			float selfRotation = -time *1f;

			matrixStack.translate(Math.sin(value * speedModifier) * 10f, 0, Math.cos(value * speedModifier) * 10f);
//			matrixStack.scale(20, 20, 20);

			matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(selfRotation));

			float SIZE = 2f;
			RandomGenerator random = entity.getWorld().random;
			double x2 = entity.getPos().getX()+.5;
			double y2 = entity.getPos().getY()+.5;
			double z2 = entity.getPos().getZ()+.5;

			VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld().setPosColorTexLightmapDefaultFormat();
			builder.setColor(new Color(0xFFFFFF))
				.setAlpha(1f)
				.renderSphere(
					vertexConsumerProvider.getBuffer(AstraWorldVFXBuilder.ADDITIVE_TEXTURE_ACTUAL_TRIANGLE.apply(new Identifier(Astronomical.MOD_ID, "textures/skybox/2.png"))),
					matrixStack,
					1,
					25,
					25);
			matrixStack.pop();

//			ParticleBuilders.create(ModParticles.STAR)
//				.setScale(SIZE/1.5f * (1 + random.nextFloat()))
//				.setAlpha(0, 0.2f, 0)
//				.setSpin((float) random.nextGaussian() / 100f)
//				.enableNoClip()
//				.setLifetime(20)
//				.setColor(1f, 1f, 1f)
//				.spawn(entity.getWorld(), x2, y2, z2);
		}
	}
}
