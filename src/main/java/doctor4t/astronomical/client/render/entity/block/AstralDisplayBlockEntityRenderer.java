package doctor4t.astronomical.client.render.entity.block;

import com.sammy.lodestone.handlers.RenderHandler;
import com.sammy.lodestone.systems.rendering.VFXBuilders;
import doctor4t.astronomical.client.AstronomicalClient;
import doctor4t.astronomical.client.render.world.AstraWorldVFXBuilder;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Unique;

import java.awt.*;

public class AstralDisplayBlockEntityRenderer<T extends AstralDisplayBlockEntity> implements BlockEntityRenderer<T> {

	@Unique
	VFXBuilders.WorldVFXBuilder builder;

	public AstralDisplayBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		this.builder = new AstraWorldVFXBuilder().setPosColorTexLightmapDefaultFormat();
	}

	@Override
	public void render(T astralDisplayBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		BlockState blockState = astralDisplayBlockEntity.getWorld().getBlockState(astralDisplayBlockEntity.getPos());

		if (blockState.isOf(ModBlocks.ASTRAL_DISPLAY) && blockState.get(AstralDisplayBlock.POWERED)) {
			float tickDelta = MinecraftClient.getInstance().isPaused() ? 0f : MinecraftClient.getInstance().getTickDelta();

			float time = ((float) (MinecraftClient.getInstance().world.getTime() % 2400000L) + tickDelta);
			matrixStack.translate(0.5f, 0.5f, 0.5f);

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
				this.builder.setColor(new Color(0x5FEBEB))
					.setAlpha(0.5f)
					.renderBeam(
						RenderHandler.EARLY_DELAYED_RENDER.getBuffer(AstronomicalClient.ASTRAL_DISPLAY_LINK),
						matrixStack,
						Vec3d.ofCenter(astralDisplayBlockEntity.getPos()),
						parentPos,
						(float) (.25f + ((Math.cos(time / 10f) + 1) / 2f) / 15f));

				this.builder.setColor(new Color(0xFFFFFF))
					.setAlpha(0.4f)
					.renderBeam(
						RenderHandler.EARLY_DELAYED_RENDER.getBuffer(AstronomicalClient.ASTRAL_DISPLAY_LINK),
						matrixStack,
						Vec3d.ofCenter(astralDisplayBlockEntity.getPos()),
						parentPos,
						(float) (.25f + ((Math.cos(time / 10f) + 1) / 2f) / 30f));

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

					matrixStack.push();

					matrixStack.translate(astralPos.x - bePos.getX(), astralPos.y - bePos.getY(), astralPos.z - bePos.getZ());

					matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(selfRotation));
					matrixStack.scale(scale, scale, scale);

					AstronomicalClient.renderAstralObject(matrixStack, vertexConsumerProvider, this.builder, stackToDisplay, circlePrecision, time, true);

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
