package doctor4t.astronomical.client.render.entity.block;

import com.sammy.lodestone.handlers.RenderHandler;
import com.sammy.lodestone.setup.LodestoneRenderLayers;
import com.sammy.lodestone.systems.rendering.VFXBuilders;
import com.terraformersmc.modmenu.util.mod.Mod;
import doctor4t.astronomical.client.AstronomicalClient;
import doctor4t.astronomical.client.render.world.AstraWorldVFXBuilder;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.block.AstralDisplayBlock;
import doctor4t.astronomical.common.block.entity.AstralDisplayBlockEntity;
import doctor4t.astronomical.common.init.ModBlocks;
import doctor4t.astronomical.common.init.ModItems;
import doctor4t.astronomical.common.item.NanoAstralObjectItem;
import doctor4t.astronomical.common.item.NanoPlanetItem;
import doctor4t.astronomical.common.item.NanoRingItem;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.random.RandomGenerator;
import org.spongepowered.asm.mixin.Unique;

import java.awt.*;

public class AstralDisplayBlockEntityRenderer<T extends AstralDisplayBlockEntity> implements BlockEntityRenderer<T> {
	// common / misc layers
	public static final RenderLayer ASTRAL_DISPLAY_LINK = LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(new Identifier(Astronomical.MOD_ID, "textures/vfx/astral_display_link.png"));
	public static final RenderLayer WHITE = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE.apply(new Identifier(Astronomical.MOD_ID, "textures/astral_object/white.png"));

	// star layer
	public static final RenderLayer STAR_1 = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE_ADDITIVE.apply(new Identifier(Astronomical.MOD_ID, "textures/astral_object/star/star_1.png"));
	public static final RenderLayer STAR_2 = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE_ADDITIVE.apply(new Identifier(Astronomical.MOD_ID, "textures/astral_object/star/star_2.png"));
	public static final RenderLayer STAR_3 = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE_ADDITIVE.apply(new Identifier(Astronomical.MOD_ID, "textures/astral_object/star/star_3.png"));

	// cosmos layers
	public static final RenderLayer BLACK = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE.apply(new Identifier(Astronomical.MOD_ID, "textures/astral_object/cosmos/black.png"));
	public static final RenderLayer STARS = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE.apply(new Identifier(Astronomical.MOD_ID, "textures/astral_object/cosmos/stars.png"));
	public static final RenderLayer STARS_FAR = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE.apply(new Identifier(Astronomical.MOD_ID, "textures/astral_object/cosmos/stars_far.png"));

	@Unique
	VFXBuilders.WorldVFXBuilder builder;

	public AstralDisplayBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		this.builder = new AstraWorldVFXBuilder().setPosColorTexLightmapDefaultFormat();
	}

	@Override
	public void render(T astralDisplayBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		RandomGenerator random = astralDisplayBlockEntity.getWorld().random;
		BlockState blockState = astralDisplayBlockEntity.getWorld().getBlockState(astralDisplayBlockEntity.getPos());
		float tickDelta = MinecraftClient.getInstance().isPaused() ? 0f : MinecraftClient.getInstance().getTickDelta();

//		VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld().setPosColorTexLightmapDefaultFormat();
		float time = ((float) (MinecraftClient.getInstance().world.getTime() % 2400000L) + tickDelta);
		matrixStack.translate(0.5f, 0.5f, 0.5f);

		float value = time;

		double distance;
		float selfRotation = (float) (-time * (astralDisplayBlockEntity.spin.getScaledValue()));
		double speedModifier;

		Vec3d bePos = Vec3d.ofCenter(astralDisplayBlockEntity.getPos());
		Vec3d parentPos;
		Vec3d orbitCenter;
		Vec3d astralPos = Vec3d.ofCenter(astralDisplayBlockEntity.getPos());

		// if connected child, render object orbiting around parent
		if (astralDisplayBlockEntity.getParentPos() != null
			&& astralDisplayBlockEntity.getWorld().getBlockState(astralDisplayBlockEntity.getParentPos()).isOf(ModBlocks.ASTRAL_DISPLAY)
			&& astralDisplayBlockEntity.getWorld().getBlockEntity(astralDisplayBlockEntity.getParentPos()) instanceof AstralDisplayBlockEntity) {

			parentPos = Vec3d.ofCenter(astralDisplayBlockEntity.getParentPos());
			orbitCenter = AstronomicalClient.ORBITING_POSITIONS.getOrDefault(astralDisplayBlockEntity.getParentPos(), Vec3d.ZERO);

			this.builder.setColor(new Color(0xFFD88F))
				.setAlpha(1f)
				.renderBeam(
					RenderHandler.LATE_DELAYED_RENDER.getBuffer(ASTRAL_DISPLAY_LINK),
					matrixStack,
					Vec3d.ofCenter(astralDisplayBlockEntity.getPos()),
					parentPos,
					(float) (.25f + ((Math.cos(time / 10f) + 1) / 2f) / 10f));

			// update orbit position hashmap
			distance = parentPos.distanceTo(bePos);
			speedModifier = astralDisplayBlockEntity.rotSpeed.getScaledValue();

			float offset = switch (blockState.get(AstralDisplayBlock.FACING)) {
				case NORTH -> 0.0F;
				case SOUTH -> (float) Math.PI;
				case WEST -> (float) Math.PI / 2f;
				case EAST -> (float) -Math.PI / 2f;
				default -> 0.0F;
			};
			astralPos = new Vec3d(orbitCenter.getX() + (Math.sin((value * speedModifier) + offset) * distance), orbitCenter.getY(), orbitCenter.getZ() + (Math.cos((value * speedModifier) + offset) * distance));
		}
		AstronomicalClient.ORBITING_POSITIONS.put(astralDisplayBlockEntity.getPos(), astralPos);

		for (int slot = 0; slot < AstralDisplayBlockEntity.SIZE; slot++) {
			ItemStack stackToDisplay = astralDisplayBlockEntity.getStack(slot);
			if (stackToDisplay.getItem() instanceof NanoAstralObjectItem) {
				float scale = stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("size") * .5f;
				int CIRCLE_PRECISION = MathHelper.clamp((int) scale * 2, 15, 50);

				matrixStack.push();

				matrixStack.translate(astralPos.x - bePos.getX(), astralPos.y - bePos.getY(), astralPos.z - bePos.getZ());

				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(selfRotation));
				matrixStack.scale(scale, scale, scale);

				if (stackToDisplay.isOf(ModItems.NANO_PLANET)) {
					int color1 = stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color1");
					int color2 = stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color2");
					RenderLayer planetRenderLayer = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE_TRANSPARENT.applyAndCache(NanoPlanetItem.PlanetTexture.byName(stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getString("texture")).texture);

					this.builder.setColor(new Color(color1))
						.setAlpha(1f)
						.renderSphere(
							vertexConsumerProvider.getBuffer(WHITE),
							matrixStack,
							1,
							CIRCLE_PRECISION,
							CIRCLE_PRECISION);

					this.builder.setColor(new Color(color2))
						.setAlpha(1f)
						.renderSphere(
							vertexConsumerProvider.getBuffer(planetRenderLayer),
							matrixStack,
							1,
							CIRCLE_PRECISION,
							CIRCLE_PRECISION);
				} else if (stackToDisplay.isOf(ModItems.NANO_STAR)) {
					int color = Astronomical.getStarColorForTemperature(stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("temperature"));

					this.builder.setColor(new Color(color).darker())
						.setAlpha(1f)
						.renderSphere(
							vertexConsumerProvider.getBuffer(WHITE),
							matrixStack,
							1,
							CIRCLE_PRECISION,
							CIRCLE_PRECISION);

					for (int layer = 1; layer <= 6; layer++) {
						float speedDiv = 5f;
						float scaleDiv = 45f;

						matrixStack.push();
						matrixStack.scale(1f + layer / scaleDiv, 1f + layer / scaleDiv, 1f + layer / scaleDiv);
						RenderLayer renderLayer = STAR_1;
						switch (layer % 6) {
							case 0 -> {
								renderLayer = STAR_1;
								matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(time / speedDiv));
							}
							case 1 -> {
								renderLayer = STAR_2;
								matrixStack.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(time / speedDiv));
							}
							case 2 -> {
								renderLayer = STAR_3;
								matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(time / speedDiv));
							}
							case 3 -> {
								renderLayer = STAR_1;
								matrixStack.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(time / speedDiv));
							}
							case 4 -> {
								renderLayer = STAR_2;
								matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(time / speedDiv));
							}
							case 5 -> {
								renderLayer = STAR_3;
								matrixStack.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(time / speedDiv));
							}
						}
						this.builder.setColor(new Color(color))
							.setAlpha(.3f).renderSphere(
								RenderHandler.LATE_DELAYED_RENDER.getBuffer(renderLayer),
								matrixStack,
								1,
								CIRCLE_PRECISION,
								CIRCLE_PRECISION);
						matrixStack.pop();
					}
				} else if (stackToDisplay.isOf(ModItems.NANO_COSMOS)) {
					this.builder.setColor(new Color(0xFFFFFF))
						.setAlpha(1f)
						.renderSphere(
							vertexConsumerProvider.getBuffer(BLACK),
							matrixStack,
							-1,
							CIRCLE_PRECISION,
							CIRCLE_PRECISION);
					this.builder.setColor(new Color(0xFFFFFF))
						.setAlpha(1f)
						.renderSphere(
							vertexConsumerProvider.getBuffer(STARS),
							matrixStack,
							-1,
							CIRCLE_PRECISION,
							CIRCLE_PRECISION);
				} else if (stackToDisplay.isOf(ModItems.NANO_RING)) {
					int color = stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color");
					RenderLayer ringRenderLayer = LodestoneRenderLayers.TRANSPARENT_TEXTURE.applyAndCache(NanoRingItem.RingTexture.byName(stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getString("texture")).texture);

					matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90f));

					this.builder.setColor(new Color(color))
						.setAlpha(1f)
						.renderQuad(
							RenderHandler.EARLY_DELAYED_RENDER.getBuffer(ringRenderLayer),
							matrixStack,
							-1
						);

					matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180f));

					this.builder.setColor(new Color(color))
						.setAlpha(1f)
						.renderQuad(
							RenderHandler.EARLY_DELAYED_RENDER.getBuffer(ringRenderLayer),
							matrixStack,
							-1
						);
				}

				matrixStack.pop();
			}
		}
	}

	@Override
	public int getRenderDistance() {
		return (MinecraftClient.getInstance().options.getEffectiveViewDistance() + 1) * 16;
	}
}
