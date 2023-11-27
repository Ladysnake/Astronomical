package doctor4t.astronomical.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.render.chunk.ChunkBuilder$BuiltChunk$RebuildTask")
public class RebuildTaskMixin {
	@Inject(method = "addBlockEntity", at = @At("HEAD"))
	private <E extends BlockEntity> void astra$cancelBlockEntity(ChunkBuilder.BuiltChunk.RebuildTask.C_iinezlaz c_iinezlaz, @NotNull E blockEntity, @NotNull CallbackInfo ci) {
		System.out.println("Block entity " + blockEntity + " at " + blockEntity.getPos());
	}
}
