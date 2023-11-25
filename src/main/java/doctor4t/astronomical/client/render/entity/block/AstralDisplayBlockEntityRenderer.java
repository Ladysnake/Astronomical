package doctor4t.astronomical.client.render.entity.block;

import com.sammy.lodestone.setup.LodestoneParticles;
import com.sammy.lodestone.setup.LodestoneRenderLayers;
import com.sammy.lodestone.systems.rendering.VFXBuilders;
import com.sammy.lodestone.systems.rendering.particle.ParticleBuilders;
import doctor4t.astronomical.client.render.world.AstraWorldVFXBuilder;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.block.AstralDisplayBlock;
import doctor4t.astronomical.common.block.entity.AstralDisplayBlockEntity;
import doctor4t.astronomical.common.init.ModBlocks;
import doctor4t.astronomical.common.init.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.random.RandomGenerator;
import org.spongepowered.asm.mixin.Unique;

import java.awt.*;

public class AstralDisplayBlockEntityRenderer<T extends AstralDisplayBlockEntity> implements BlockEntityRenderer<T> {
	public static final RenderLayer ASTRAL_DISPLAY_LINK = LodestoneRenderLayers.ADDITIVE_TEXTURE.apply(new Identifier(Astronomical.MOD_ID, "textures/vfx/astral_display_link.png"));

	@Unique
	VFXBuilders.WorldVFXBuilder builder;

	public AstralDisplayBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		builder = VFXBuilders.createWorld().setPosColorTexLightmapDefaultFormat();
	}

	public void render(T astralDisplayBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		RandomGenerator random = astralDisplayBlockEntity.getWorld().random;
		BlockState blockState = astralDisplayBlockEntity.getWorld().getBlockState(astralDisplayBlockEntity.getPos());

//		VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld().setPosColorTexLightmapDefaultFormat();
		float time = ((float) (MinecraftClient.getInstance().world.getTime() % 2400000L) + MinecraftClient.getInstance().getTickDelta());

		// if connected child, render object orbiting around parent
		if (astralDisplayBlockEntity.getParentPos() != null
			&& astralDisplayBlockEntity.getWorld().getBlockState(astralDisplayBlockEntity.getParentPos()).isOf(ModBlocks.ASTRAL_DISPLAY)
			&& astralDisplayBlockEntity.getWorld().getBlockState(astralDisplayBlockEntity.getParentPos()).get(AstralDisplayBlock.FACING).equals(Direction.UP)
			&& astralDisplayBlockEntity.getWorld().getBlockEntity(astralDisplayBlockEntity.getParentPos()) instanceof AstralDisplayBlockEntity parentAstralDisplayBlockEntity) {
			matrixStack.translate(0.5f, 0.5f, 0.5f);

			builder.setColor(new Color(0xFFD88F))
				.setAlpha(1f)
				.renderBeam(
					vertexConsumerProvider.getBuffer(ASTRAL_DISPLAY_LINK),
					matrixStack,
					Vec3d.ofCenter(astralDisplayBlockEntity.getPos()),
					Vec3d.ofCenter(astralDisplayBlockEntity.getParentPos()),
					(float) (.25f+((Math.cos(time/10f)+1)/2f)/10f));

			// render test orbiting planet
			for (int slot = 0; slot < AstralDisplayBlockEntity.SIZE; slot++) {
				ItemStack stackToDisplay = astralDisplayBlockEntity.getStack(slot);
				if (stackToDisplay.isOf(ModItems.NANO_PLANET)) {
					int color = stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color");
					float value = time;
					float scale = stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("size") * .5f;
					float distance = astralDisplayBlockEntity.getParentPos().getManhattanDistance(astralDisplayBlockEntity.getPos());
					float selfRotation = -time * (distance/10f);
					float speedModifier = 0.001f * distance;

					matrixStack.push();
					matrixStack.translate(-(astralDisplayBlockEntity.getPos().getX() - astralDisplayBlockEntity.getParentPos().getX()),
						-(astralDisplayBlockEntity.getPos().getY() - astralDisplayBlockEntity.getParentPos().getY()),
						-(astralDisplayBlockEntity.getPos().getZ() - astralDisplayBlockEntity.getParentPos().getZ()));
					matrixStack.translate(Math.sin(value * speedModifier) * distance, 0, Math.cos(value * speedModifier) * distance);
					matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(selfRotation));
					matrixStack.scale(scale, scale, scale);

					builder.setColor(new Color(color))
						.setAlpha(1f)
						.renderSphere(
							vertexConsumerProvider.getBuffer(AstraWorldVFXBuilder.ADDITIVE_TEXTURE_ACTUAL_TRIANGLE.apply(new Identifier(Astronomical.MOD_ID, "textures/skybox/2.png"))),
							matrixStack,
							1,
							20,
							20);
					matrixStack.pop();
				} else if (stackToDisplay.isOf(ModItems.NANO_STAR)) {

				}
			}
		}

		// if parent render static object
		if (blockState.isOf(ModBlocks.ASTRAL_DISPLAY) && blockState.get(AstralDisplayBlock.FACING).equals(Direction.UP)) {
			float SIZE = 2f;
			double x2 = astralDisplayBlockEntity.getPos().getX() + .5;
			double y2 = astralDisplayBlockEntity.getPos().getY() + .5;
			double z2 = astralDisplayBlockEntity.getPos().getZ() + .5;

			ParticleBuilders.create(LodestoneParticles.WISP_PARTICLE)
				.setScale(SIZE / 1.5f * (1 + random.nextFloat()))
				.setAlpha(0, 0.2f, 0)
				.setSpin((float) random.nextGaussian() / 100f)
				.enableNoClip()
				.setLifetime(20)
				.setColor(1f, 1f, 1f)
				.spawn(astralDisplayBlockEntity.getWorld(), x2, y2, z2);


			matrixStack.push();
			matrixStack.translate(.5f, .5f, .5f);
			builder.setColor(new Color(0xFFFFFF))
				.setAlpha(1f)
				.renderSphere(
					vertexConsumerProvider.getBuffer(AstraWorldVFXBuilder.ADDITIVE_TEXTURE_ACTUAL_TRIANGLE.apply(new Identifier(Astronomical.MOD_ID, "textures/skybox/white.png"))),
					matrixStack,
					1,
					20,
					20);
			matrixStack.pop();


//		/* TEST STARFIELD RENDER
			matrixStack.push();
			matrixStack.translate(.5f, .5f, .5f);
			matrixStack.scale(20f, 20f, 20f);
			matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-time * .1f));
			builder.setColor(new Color(0xFFFFFF))
				.setAlpha(1f)
				.renderSphere(
					vertexConsumerProvider.getBuffer(AstraWorldVFXBuilder.ADDITIVE_TEXTURE_ACTUAL_TRIANGLE.apply(new Identifier(Astronomical.MOD_ID, "textures/skybox/stars.png"))),
					matrixStack,
					-1,
					50,
					50);

			matrixStack.scale(1.01f, 1.01f, 1.01f);
			builder.setColor(new Color(0xFFFFFF))
				.setAlpha(1f)
				.renderSphere(
					vertexConsumerProvider.getBuffer(AstraWorldVFXBuilder.ADDITIVE_TEXTURE_ACTUAL_TRIANGLE.apply(new Identifier(Astronomical.MOD_ID, "textures/skybox/white.png"))),
					matrixStack,
					-1,
					50,
					50);
			matrixStack.pop();

//		*/
		}
	}
}
