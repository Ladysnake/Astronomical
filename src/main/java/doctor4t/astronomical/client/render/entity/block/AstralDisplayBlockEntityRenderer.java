package doctor4t.astronomical.client.render.entity.block;

import com.sammy.lodestone.handlers.RenderHandler;
import com.sammy.lodestone.setup.LodestoneRenderLayers;
import com.sammy.lodestone.systems.rendering.VFXBuilders;
import doctor4t.astronomical.client.AstronomicalClient;
import doctor4t.astronomical.client.render.world.AstraSkyRenderer;
import doctor4t.astronomical.client.render.world.AstraWorldVFXBuilder;
import doctor4t.astronomical.client.render.world.VertexData;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.block.AstralDisplayBlock;
import doctor4t.astronomical.common.block.entity.AstralDisplayBlockEntity;
import doctor4t.astronomical.common.init.ModBlocks;
import doctor4t.astronomical.common.item.NanoAstralObjectItem;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.Unique;

import java.awt.*;

public class AstralDisplayBlockEntityRenderer<T extends AstralDisplayBlockEntity> implements BlockEntityRenderer<T> {

	@Unique
	VFXBuilders.WorldVFXBuilder builder;

	public AstralDisplayBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		this.builder = new AstraWorldVFXBuilder().setPosColorTexLightmapDefaultFormat();
	}

	@Override
	public void render(T astralDisplayBlockEntity, float f, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		BlockState blockState = astralDisplayBlockEntity.getWorld().getBlockState(astralDisplayBlockEntity.getPos());

		if (blockState.isOf(ModBlocks.ASTRAL_DISPLAY) && blockState.get(AstralDisplayBlock.POWERED)) {
			float tickDelta = MinecraftClient.getInstance().isPaused() ? 0f : MinecraftClient.getInstance().getTickDelta();

			float time = ((float) (MinecraftClient.getInstance().world.getTime() % 2400000L) + tickDelta);
			matrices.translate(0.5f, 0.5f, 0.5f);

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

				// astral link connecting displays
				double v = bePos.distanceTo(parentPos);
				for (int k = 0; k < v; k++) {
					Vec3d nextPos = Vec3d.ofCenter(astralDisplayBlockEntity.getPos().offset(blockState.get(AstralDisplayBlock.FACING), k));
					BlockPos nextNextBP = astralDisplayBlockEntity.getPos().offset(blockState.get(AstralDisplayBlock.FACING), k+1);
					Vec3d nextNextPos = Vec3d.ofCenter(nextNextBP);

					VFXBuilders.WorldVFXBuilder builder = new AstraWorldVFXBuilder().setPosColorTexLightmapDefaultFormat();
					MinecraftClient client = MinecraftClient.getInstance();
					Vec3d playerPos = client.player != null ? client.player.getCameraPosVec(tickDelta) : Vec3d.ZERO;
					Vec3d diff = nextPos.subtract(playerPos);
					Vec3d dirVec = nextNextPos.subtract(nextPos);
					Color color = Astronomical.STAR_PURPLE;
					VertexData d = AstraWorldVFXBuilder.createFadeoutVertexData(diff, dirVec, .1f,.1f, color, color.getAlpha(), -(astralDisplayBlockEntity.getWorld().getTime() + tickDelta % 190) / 190f);

					((AstraWorldVFXBuilder) builder.setOffset((float) ((float) -bePos.getX() + playerPos.getX()), (float) ((float) -bePos.getY() + playerPos.getY()), (float) ((float) -bePos.getZ() + playerPos.getZ()))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(AstraSkyRenderer.SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);

					diff = nextPos.subtract(playerPos).add(-0.0, -0.02, -0.0);

					d = AstraWorldVFXBuilder.createFadeoutVertexData(diff, dirVec, .1f,.1f, color, color.getAlpha(), (-(astralDisplayBlockEntity.getWorld().getTime() + tickDelta % 190) / 190f + 0.1f) * 4f);

					((AstraWorldVFXBuilder) builder.setOffset((float) ((float) -bePos.getX() + playerPos.getX()), (float) ((float) -bePos.getY() + playerPos.getY()), (float) ((float) -bePos.getZ() + playerPos.getZ()))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(AstraSkyRenderer.SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);

					diff = nextPos.subtract(playerPos).add(0.0, 0.02, 0.0);

					d = AstraWorldVFXBuilder.createFadeoutVertexData(diff, dirVec, .1f,.1f, color, color.getAlpha(), (-(astralDisplayBlockEntity.getWorld().getTime() + tickDelta % 190) / 190f + 0.6f) * 2f);

					((AstraWorldVFXBuilder) builder.setOffset((float) ((float) -bePos.getX() + playerPos.getX()), (float) ((float) -bePos.getY() + playerPos.getY()), (float) ((float) -bePos.getZ() + playerPos.getZ()))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(AstraSkyRenderer.SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);

					if (astralDisplayBlockEntity.getWorld().getBlockState(nextNextBP).isOf(ModBlocks.ASTRAL_DISPLAY)) {
						break;
					}
				}

				// update orbit position hashmap
				distance = parentPos.distanceTo(bePos);
				speedModifier = astralDisplayBlockEntity.rotSpeed.getScaledValue();

				float offset = switch (blockState.get(AstralDisplayBlock.FACING)) {
					case SOUTH -> (float) Math.PI;
					case WEST -> (float) Math.PI / 2f;
					case EAST -> (float) -Math.PI / 2f;
					default -> 0.0F;
				};
				astralPos = new Vec3d(orbitCenter.getX() + (Math.sin((time * speedModifier) + offset) * distance), orbitCenter.getY(), orbitCenter.getZ() + (Math.cos((time * speedModifier) + offset) * distance));
			}
			AstronomicalClient.ORBITING_POSITIONS.put(astralDisplayBlockEntity.getPos(), astralPos);

			for (int slot = 0; slot < AstralDisplayBlockEntity.SIZE; slot++) {
				ItemStack stackToDisplay = astralDisplayBlockEntity.getStack(slot);
				if (stackToDisplay.getItem() instanceof NanoAstralObjectItem) {
					float scale = stackToDisplay.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("size") * .5f;
					int circlePrecision = MathHelper.clamp((int) scale * 2, 15, 50);

					matrices.push();

					matrices.translate(astralPos.x - bePos.getX(), astralPos.y - bePos.getY(), astralPos.z - bePos.getZ());

					matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(selfRotation));
					matrices.scale(scale, scale, scale);

					AstronomicalClient.renderAstralObject(matrices, vertexConsumerProvider, this.builder, stackToDisplay, circlePrecision, time, true);

					matrices.pop();
				}
			}
		}
	}

	@Override
	public int getRenderDistance() {
		return (MinecraftClient.getInstance().options.getEffectiveViewDistance() + 1) * 16;
	}
}
