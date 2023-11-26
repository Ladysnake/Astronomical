package doctor4t.astronomical.mixin;

import doctor4t.astronomical.common.item.MarshmallowStickItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
	@Shadow @Final private MinecraftClient client;
	@Shadow private int scaledWidth;
	@Shadow private int scaledHeight;

	@Inject(method = "renderCrosshair", at = @At("TAIL"))
	private void astronomical$renderCrosshair(MatrixStack matrices, CallbackInfo ci) {
		var gameOptions = this.client.options;
		if (!gameOptions.getPerspective().isFirstPerson() || this.client.interactionManager == null || this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
			return;
		}
		var player = this.client.player;
		if (player == null || !(player.getMainHandStack().getItem() instanceof MarshmallowStickItem)) {
			return;
		}

		if (player.astronomical$isHoldingAttack()) {
			var j = this.scaledHeight / 2 - 7 + 16;
			var k = this.scaledWidth / 2 - 8;
			this.drawTexture(matrices, k, j + 20, 36, 94, 16, 4);
		}
	}
}
