package doctor4t.astronomical.client.render.entity.block;

import com.sammy.lodestone.handlers.RenderHandler;
import com.sammy.lodestone.setup.LodestoneRenderLayers;
import com.sammy.lodestone.systems.rendering.VFXBuilders;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.random.RandomGenerator;
import org.spongepowered.asm.mixin.Unique;

import java.awt.*;

public class AstralDisplayBlockEntityRenderer<T extends AstralDisplayBlockEntity> implements BlockEntityRenderer<T> {
	public static final RenderLayer ASTRAL_DISPLAY_LINK = LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(new Identifier(Astronomical.MOD_ID, "textures/vfx/astral_display_link.png"));
	public static final RenderLayer WHITE = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE.apply(new Identifier(Astronomical.MOD_ID, "textures/skybox/white.png"));
	public static final RenderLayer WHITE_ADDITIVE = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE_ADDITIVE.apply(new Identifier(Astronomical.MOD_ID, "textures/skybox/white.png"));
	public static final RenderLayer PLANET_1 = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE_TRANSPARENT.apply(new Identifier(Astronomical.MOD_ID, "textures/skybox/planet_1.png"));
	public static final RenderLayer PLANET_1_ADDITIVE = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE_ADDITIVE.apply(new Identifier(Astronomical.MOD_ID, "textures/skybox/planet_1.png"));
	public static final RenderLayer STARS = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE.apply(new Identifier(Astronomical.MOD_ID, "textures/skybox/stars.png"));

	@Unique
	VFXBuilders.WorldVFXBuilder builder;

	public AstralDisplayBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		this.builder = new AstraWorldVFXBuilder().setPosColorTexLightmapDefaultFormat();
	}

	@Override
	public void render(T astralDisplayBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		RandomGenerator random = astralDisplayBlockEntity.getWorld().random;
		BlockState blockState = astralDisplayBlockEntity.getWorld().getBlockState(astralDisplayBlockEntity.getPos());

//		VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld().setPosColorTexLightmapDefaultFormat();
		float time = ((float) (MinecraftClient.getInstance().world.getTime() % 2400000L) + MinecraftClient.getInstance().getTickDelta());
		matrixStack.translate(0.5f, 0.5f, 0.5f);

		BlockPos parentPos = astralDisplayBlockEntity.getPos();
		// if connected child, render object orbiting around parent
		if (astralDisplayBlockEntity.getParentPos() != null
			&& astralDisplayBlockEntity.getWorld().getBlockState(astralDisplayBlockEntity.getParentPos()).isOf(ModBlocks.ASTRAL_DISPLAY)
			&& astralDisplayBlockEntity.getWorld().getBlockState(astralDisplayBlockEntity.getParentPos()).get(AstralDisplayBlock.FACING).equals(Direction.UP)
			&& astralDisplayBlockEntity.getWorld().getBlockEntity(astralDisplayBlockEntity.getParentPos()) instanceof AstralDisplayBlockEntity parentAstralDisplayBlockEntity) {

			parentPos = astralDisplayBlockEntity.getParentPos();

			this.builder.setColor(new Color(0xFFD88F))
				.setAlpha(1f)
				.renderBeam(
					RenderHandler.LATE_DELAYED_RENDER.getBuffer(ASTRAL_DISPLAY_LINK),
					matrixStack,
					Vec3d.ofCenter(astralDisplayBlockEntity.getPos()),
					Vec3d.ofCenter(parentPos),
					(float) (.25f + ((Math.cos(time / 10f) + 1) / 2f) / 10f));
		} else {

			// TEST COSMOS RENDER
//			matrixStack.push();
//			matrixStack.translate(.5f, .5f, .5f);
//			matrixStack.scale(50f, 50f, 50f);
//			matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-time * .01f));
//			this.builder.setColor(new Color(0xFFFFFF))
//				.setAlpha(1f)
//				.renderSphere(
//					vertexConsumerProvider.getBuffer(STARS),
//					matrixStack,
//					-1,
//					50,
//					50);
//			matrixStack.scale(1.01f, 1.01f, 1.01f);
//			this.builder.setColor(new Color(0xFFFFFF))
//				.setAlpha(1f)
//				.renderSphere(
//					vertexConsumerProvider.getBuffer(WHITE),
//					matrixStack,
//					-1,
//					50,
//					50);
//			matrixStack.pop();
		}

		float value = time;
		float distance = parentPos.getManhattanDistance(astralDisplayBlockEntity.getPos());
		float selfRotation = -time * (distance / 100f);
		float speedModifier = 0.0001f * distance;

		for (int slot = 0; slot < AstralDisplayBlockEntity.SIZE; slot++) {
			ItemStack stackToDisplay = astralDisplayBlockEntity.getStack(slot);
			if (!stackToDisplay.isEmpty()) {
				float scale = stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("size") * .5f;

				if (stackToDisplay.isOf(ModItems.NANO_PLANET)) {
					int color1 = stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color1");
					int color2 = stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color2");

					matrixStack.push();
					matrixStack.translate(-(astralDisplayBlockEntity.getPos().getX() - parentPos.getX()),
						-(astralDisplayBlockEntity.getPos().getY() - parentPos.getY()),
						-(astralDisplayBlockEntity.getPos().getZ() - parentPos.getZ()));
					matrixStack.translate(Math.sin(value * speedModifier) * distance, 0, Math.cos(value * speedModifier) * distance);
					matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(selfRotation));
					matrixStack.scale(scale, scale, scale);

					this.builder.setColor(new Color(color1))
						.setAlpha(1f)
						.renderSphere(
							vertexConsumerProvider.getBuffer(WHITE),
							matrixStack,
							1,
							20,
							20);

					this.builder.setColor(new Color(color2))
						.setAlpha(1f)
						.renderSphere(
							vertexConsumerProvider.getBuffer(PLANET_1),
							matrixStack,
							1,
							20,
							20);

					matrixStack.pop();
				} else if (stackToDisplay.isOf(ModItems.NANO_STAR)) {
					int color = Astronomical.getStarColorForTemperature(stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("temperature"));

					matrixStack.push();

					matrixStack.translate(-(astralDisplayBlockEntity.getPos().getX() - parentPos.getX()),
						-(astralDisplayBlockEntity.getPos().getY() - parentPos.getY()),
						-(astralDisplayBlockEntity.getPos().getZ() - parentPos.getZ()));
					matrixStack.translate(Math.sin(value * speedModifier) * distance, 0, Math.cos(value * speedModifier) * distance);

					matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(selfRotation));
					matrixStack.scale(scale, scale, scale);

					this.builder.setColor(new Color(color).darker())
						.setAlpha(1f)
						.renderSphere(
							vertexConsumerProvider.getBuffer(WHITE),
							matrixStack,
							1,
							20,
							20);

					for (int layer = 1; layer < 6; layer++) {
						float speedDiv = 5f;
						float scaleDiv = 50f;

						matrixStack.push();
						matrixStack.scale(1f + layer / scaleDiv, 1f + layer / scaleDiv, 1f + layer / scaleDiv);
						switch (layer % 6) {
							case 0 -> matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(time / speedDiv));
							case 1 -> matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(time / speedDiv));
							case 2 -> matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(time / speedDiv));
							case 3 -> matrixStack.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(time / speedDiv));
							case 4 -> matrixStack.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(time / speedDiv));
							case 5 -> matrixStack.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(time / speedDiv));
						}
						this.builder.setColor(new Color(color))
							.setAlpha(.2f)
							.renderSphere(
								RenderHandler.LATE_DELAYED_RENDER.getBuffer(PLANET_1_ADDITIVE),
								matrixStack,
								1,
								20,
								20);
						matrixStack.pop();
					}

					matrixStack.pop();
				}
			}
		}
	}

	@Override
	public int getRenderDistance() {
		return (MinecraftClient.getInstance().options.getEffectiveViewDistance() + 1) * 16;
	}
}
