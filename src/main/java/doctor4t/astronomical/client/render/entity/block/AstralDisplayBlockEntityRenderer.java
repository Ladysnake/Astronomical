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
import doctor4t.astronomical.common.structure.InteractableStar;
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

import static doctor4t.astronomical.client.render.world.AstraSkyRenderer.rotateViaQuat;

public class AstralDisplayBlockEntityRenderer<T extends AstralDisplayBlockEntity> implements BlockEntityRenderer<T> {
	private static final Vec3d UP = new Vec3d(0, 1, 0);

	@Unique
	VFXBuilders.WorldVFXBuilder builder;

	public AstralDisplayBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		this.builder = new AstraWorldVFXBuilder().setPosColorTexLightmapDefaultFormat();
	}

	private static VertexData createVertexData(Vec3d dir, Vec3d up, float size, float rotation, Color c) {
		float x = (float) dir.x;
		float y = (float) dir.y;
		float z = (float) dir.z;

		if (Math.abs(dir.dotProduct(up)) >= 1) {
			up = new Vec3d(0, 0, 1);
		}

		float ux = (float) up.x;
		float uy = (float) up.y;
		float uz = (float) up.z;

		float f = MathHelper.sin(rotation / 2.0F);
		float qx = x * f;
		float qy = y * f;
		float qz = z * f;
		float qw = MathHelper.cos(rotation / 2.0F);

		float t1x = y * uz - (z * uy);
		float t1y = z * ux - (x * uz);
		float t1z = x * uy - (y * ux);

		Vec3d q = rotateViaQuat(t1x, t1y, t1z, qx, qy, qz, qw);
		t1x = (float) q.x;
		t1y = (float) q.y;
		t1z = (float) q.z;

		float t1d2 = (float) Math.sqrt(AstraWorldVFXBuilder.distanceSquared(t1x, t1y, t1z));

		t1x /= t1d2;
		t1y /= t1d2;
		t1z /= t1d2;

		float t2x = y * t1z - (z * t1y);
		float t2y = z * t1x - (x * t1z);
		float t2z = x * t1y - (y * t1x);

		float t2d2 = (float) Math.sqrt(AstraWorldVFXBuilder.distanceSquared(t2x, t2y, t2z));

		t2x /= t2d2;
		t2y /= t2d2;
		t2z /= t2d2;
		t1x *= size;
		t1y *= size;
		t1z *= size;
		t2x *= size;
		t2y *= size;
		t2z *= size;
		x *= 0.501f;
		y *= 0.501f;
		z *= 0.501f;

		return new VertexData(new Vec3f[]{new Vec3f(x + t1x + t2x, y + t1y + t2y, z + t1z + t2z), new Vec3f(x - t1x + t2x, y - t1y + t2y, z - t1z + t2z), new Vec3f(x - t1x - t2x, y - t1y - t2y, z - t1z - t2z), new Vec3f(x + t1x - t2x, y + t1y - t2y, z + t1z - t2z)}, new Color[]{c, c, c, c}, new Vec2f[]{new Vec2f(0, 1), new Vec2f(1, 1), new Vec2f(1, 0), new Vec2f(0, 0)});
	}

	@Override
	public void render(T astralDisplayBlockEntity, float f, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		BlockState blockState = astralDisplayBlockEntity.getWorld().getBlockState(astralDisplayBlockEntity.getPos());

		if (blockState.isOf(ModBlocks.ASTRAL_DISPLAY) && blockState.get(AstralDisplayBlock.POWERED)) {
			float tickDelta = MinecraftClient.getInstance().isPaused() ? 0f : MinecraftClient.getInstance().getTickDelta();

			float time = ((float) (MinecraftClient.getInstance().world.getTime() % 2400000L) + tickDelta);

			matrices.translate(0.5f, 0.5f, 0.5f);

			Vec3f flo = blockState.get(AstralDisplayBlock.FACING).getUnitVector();
			VertexData data = createVertexData(new Vec3d(flo.getX(), flo.getY(), flo.getZ()), UP, 0.45f + 0.05f * MathHelper.sin(time / 16), time / 32, Astronomical.STAR_PURPLE);

			((AstraWorldVFXBuilder) builder.setAlpha(0.9f)).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(InteractableStar.INTERACTABLE_TEX)), matrices, data, builder::setPosColorTexLightmapDefaultFormat);

			data = createVertexData(new Vec3d(flo.getX(), flo.getY(), flo.getZ()), UP, 0.4f + 0.1f * MathHelper.sin(-time / 17), -time / 37, Astronomical.STAR_PURPLE);

			((AstraWorldVFXBuilder) builder.setAlpha(0.9f)).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(InteractableStar.INTERACTABLE_TEX)), matrices, data, builder::setPosColorTexLightmapDefaultFormat);

			data = createVertexData(new Vec3d(flo.getX(), flo.getY(), flo.getZ()), UP, 0.6f, 0, Astronomical.STAR_PURPLE);

			((AstraWorldVFXBuilder) builder.setAlpha(0.9f)).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(InteractableStar.INTERACTABLE_TEX)), matrices, data, builder::setPosColorTexLightmapDefaultFormat);

			double distance;
			float selfRotation = (float) (-time * (astralDisplayBlockEntity.spin.getScaledValue()));
			double speedModifier;

			Vec3d bePos = Vec3d.ofCenter(astralDisplayBlockEntity.getPos());
			Vec3d parentPos;
			Vec3d orbitCenter;
			Vec3d astralPos = Vec3d.ofCenter(astralDisplayBlockEntity.getPos()).add(0, astralDisplayBlockEntity.yLevel.getScaledValue(), 0);

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
					BlockPos nextNextBP = astralDisplayBlockEntity.getPos().offset(blockState.get(AstralDisplayBlock.FACING), k + 1);
					Vec3d nextNextPos = Vec3d.ofCenter(nextNextBP);

					VFXBuilders.WorldVFXBuilder builder = new AstraWorldVFXBuilder().setPosColorTexLightmapDefaultFormat();
					MinecraftClient client = MinecraftClient.getInstance();
					Vec3d playerPos = client.player != null ? client.player.getCameraPosVec(tickDelta) : Vec3d.ZERO;
					Vec3d diff = nextPos.subtract(playerPos);
					Vec3d dirVec = nextNextPos.subtract(nextPos);
					Color color = Astronomical.STAR_PURPLE;
					VertexData d = AstraWorldVFXBuilder.createFadeoutVertexData(diff, dirVec, .1f, .1f, color, color.getAlpha(), -(astralDisplayBlockEntity.getWorld().getTime() + tickDelta % 190) / 190f);

					((AstraWorldVFXBuilder) builder.setOffset((float) ((float) -bePos.getX() + playerPos.getX()), (float) ((float) -bePos.getY() + playerPos.getY()), (float) ((float) -bePos.getZ() + playerPos.getZ()))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(AstraSkyRenderer.SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);

					diff = nextPos.subtract(playerPos).add(-0.0, -0.02, -0.0);

					d = AstraWorldVFXBuilder.createFadeoutVertexData(diff, dirVec, .1f, .1f, color, color.getAlpha(), (-(astralDisplayBlockEntity.getWorld().getTime() + tickDelta % 190) / 190f + 0.1f) * 4f);

					((AstraWorldVFXBuilder) builder.setOffset((float) ((float) -bePos.getX() + playerPos.getX()), (float) ((float) -bePos.getY() + playerPos.getY()), (float) ((float) -bePos.getZ() + playerPos.getZ()))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(AstraSkyRenderer.SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);

					diff = nextPos.subtract(playerPos).add(0.0, 0.02, 0.0);

					d = AstraWorldVFXBuilder.createFadeoutVertexData(diff, dirVec, .1f, .1f, color, color.getAlpha(), (-(astralDisplayBlockEntity.getWorld().getTime() + tickDelta % 190) / 190f + 0.6f) * 2f);

					((AstraWorldVFXBuilder) builder.setOffset((float) ((float) -bePos.getX() + playerPos.getX()), (float) ((float) -bePos.getY() + playerPos.getY()), (float) ((float) -bePos.getZ() + playerPos.getZ()))).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(AstraSkyRenderer.SHIMMER)), matrices, d, builder::setPosColorTexLightmapDefaultFormat);

					BlockState bs = astralDisplayBlockEntity.getWorld().getBlockState(nextNextBP);
					if (bs.isOf(ModBlocks.ASTRAL_DISPLAY) && bs.get(AstralDisplayBlock.POWERED)) {
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
