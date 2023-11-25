package doctor4t.astronomical.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import doctor4t.astronomical.client.render.world.AstraSkyRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Shadow
	@Nullable
	private ClientWorld world;

	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	@Nullable
	private VertexBuffer lightSkyBuffer;

	@Shadow
	@Nullable
	private VertexBuffer darkSkyBuffer;

	@Shadow
	@Nullable
	private VertexBuffer starsBuffer;

	@Shadow
	@Nullable
	private Frustum capturedFrustum;

	@Shadow
	private Frustum frustum;

	@Shadow
	@Final
	private Vector3d capturedFrustumPosition;

	@Shadow
	@Final
	private BufferBuilderStorage bufferBuilders;

	@Inject(at = @At(value = "INVOKE", target = "Ljava/lang/Runnable;run()V", shift = At.Shift.AFTER), method = "renderSky", cancellable = true)
	private void renderPFSky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera preStep, boolean bl, Runnable runnable, CallbackInfo ci) {
		if (this.world.getRegistryKey().equals(World.OVERWORLD)) {
			boolean bl2 = this.capturedFrustum != null;
			Frustum frustum;
			if (bl2) {
				frustum = this.capturedFrustum;
				frustum.setPosition(this.capturedFrustumPosition.x, this.capturedFrustumPosition.y, this.capturedFrustumPosition.z);
			} else {
				frustum = this.frustum;
			}
			RenderSystem.depthMask(false);
			AstraSkyRenderer.renderSky(matrices, this.bufferBuilders.getEntityVertexConsumers(), projectionMatrix, frustum, tickDelta, runnable, this.world, this.client);
			runnable.run();
			RenderSystem.depthMask(true);
			RenderSystem.disableBlend();
			ci.cancel();
		}
	}
}
