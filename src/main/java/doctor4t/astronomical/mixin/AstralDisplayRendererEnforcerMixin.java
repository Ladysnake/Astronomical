package doctor4t.astronomical.mixin;

import doctor4t.astronomical.common.block.entity.AstralDisplayBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.render.chunk.ChunkBuilder$BuiltChunk$RebuildTask")
public class AstralDisplayRendererEnforcerMixin {
	@Inject(method = "addBlockEntity", at = @At("HEAD"), cancellable = true)
	private <E extends BlockEntity> void astronomical$forceAstralDisplayRendering(ChunkBuilder.BuiltChunk.RebuildTask.C_iinezlaz c_iinezlaz, @NotNull E blockEntity, @NotNull CallbackInfo ci) {
		if (blockEntity instanceof AstralDisplayBlockEntity) {
			var blockEntityRenderer = MinecraftClient.getInstance().getBlockEntityRenderDispatcher().get(blockEntity);
			if (blockEntityRenderer != null) {
				c_iinezlaz.field_39079.add(blockEntity);
			}
			ci.cancel();
		}
	}
}
