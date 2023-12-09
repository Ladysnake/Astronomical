package doctor4t.astronomical.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class DisableCrosshairWhenUsingSpyglassInGameHudMixin {
	@Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
	private void renderCrosshair(MatrixStack matrices, CallbackInfo ci) {
		if (MinecraftClient.getInstance().player.isUsingSpyglass()) {
			ci.cancel();
		}
	}
}
