package doctor4t.astronomical.client.render.entity.block;

import doctor4t.astronomical.client.AstronomicalClient;
import doctor4t.astronomical.client.render.world.AstraWorldVFXBuilder;
import doctor4t.astronomical.client.render.world.VertexData;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.block.AstralDisplayBlock;
import doctor4t.astronomical.common.block.entity.AstralDisplayBlockEntity;
import doctor4t.astronomical.common.init.ModBlocks;
import doctor4t.astronomical.common.structure.InteractableStar;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Unique;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.setup.LodestoneRenderLayers;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;

public class AstralDisplayBlockEntityRenderer<T extends AstralDisplayBlockEntity> implements BlockEntityRenderer<T> {

	@Unique
	VFXBuilders.WorldVFXBuilder builder;

	public AstralDisplayBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		this.builder = new AstraWorldVFXBuilder().setPosColorTexLightmapDefaultFormat();
	}

	@Override
	public void render(T astralDisplayBlockEntity, float f, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		if (!AstronomicalClient.ASTRAL_OBJECTS_TO_RENDER.containsKey(astralDisplayBlockEntity.getPos())) {
			AstronomicalClient.ASTRAL_OBJECTS_TO_RENDER.put(astralDisplayBlockEntity.getPos(), Vec3d.ofCenter(astralDisplayBlockEntity.getPos()));
		}

		float tickDelta = MinecraftClient.getInstance().isPaused() ? 0f : MinecraftClient.getInstance().getTickDelta();
		float time = ((float) (MinecraftClient.getInstance().world.getTime() % 2400000L) + tickDelta);
		BlockPos blockPos = astralDisplayBlockEntity.getPos();
		World world = astralDisplayBlockEntity.getWorld();
		BlockState blockState = world.getBlockState(blockPos);
		BlockEntity blockEntity = world.getBlockEntity(blockPos);

		if (blockEntity != null && blockState.isOf(ModBlocks.ASTRAL_DISPLAY) && blockState.get(AstralDisplayBlock.POWERED)) {
			matrices.push();
			matrices.translate(0.5f, 0.5f, 0.5f);

			Vector3f flo = blockState.get(AstralDisplayBlock.FACING).getUnitVector();
			VertexData data = AstronomicalClient.createVertexData(new Vec3d(flo.x(), flo.y(), flo.z()), AstronomicalClient.UP, 0.45f + 0.05f * MathHelper.sin(time / 16), time / 32, Astronomical.STAR_PURPLE);

			((AstraWorldVFXBuilder) builder.setAlpha(0.9f)).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(InteractableStar.INTERACTABLE_TEX)), matrices, data, builder::setPosColorTexLightmapDefaultFormat);

			data = AstronomicalClient.createVertexData(new Vec3d(flo.x(), flo.y(), flo.z()), AstronomicalClient.UP, 0.4f + 0.1f * MathHelper.sin(-time / 17), -time / 37, Astronomical.STAR_PURPLE);

			((AstraWorldVFXBuilder) builder.setAlpha(0.9f)).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(InteractableStar.INTERACTABLE_TEX)), matrices, data, builder::setPosColorTexLightmapDefaultFormat);

			data = AstronomicalClient.createVertexData(new Vec3d(flo.x(), flo.y(), flo.z()), AstronomicalClient.UP, 0.6f, 0, Astronomical.STAR_PURPLE);

			((AstraWorldVFXBuilder) builder.setAlpha(0.9f)).renderQuad(RenderHandler.DELAYED_RENDER.getBuffer(LodestoneRenderLayers.ADDITIVE_TEXTURE.applyAndCache(InteractableStar.INTERACTABLE_TEX)), matrices, data, builder::setPosColorTexLightmapDefaultFormat);

			matrices.pop();
		}
	}

	@Override
	public int getRenderDistance() {
		return (MinecraftClient.getInstance().options.getEffectiveViewDistance() + 1) * 16;
	}
}
