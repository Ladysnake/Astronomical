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

	@Inject(method = "renderCrosshair", at = @At("HEAD"))
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
			var width = MarshmallowStickItem.CookState.values()[MarshmallowStickItem.CookState.values().length - 1].cookTime / 10 + 6;
			var x = this.scaledWidth / 2 - width / 2;
			var y = this.scaledHeight / 2 + 9;
			this.fillGradient(matrices, x - 2, y, x + width, y + 6, 0xFF000000, 0xFF000000);
			for (var i = MarshmallowStickItem.CookState.values().length - 1; i >= 0; i--) {
				var state = MarshmallowStickItem.CookState.values()[i];
				var stateWidth = state == MarshmallowStickItem.CookState.BURNT ? state.cookTime / 10 + 6 : state.next().cookTime / 10;
				this.fillGradient(matrices, x, y, x + stateWidth, y + 4, state.color, state.color);
			}
			var progress = player.getMainHandStack().getOrCreateNbt().getInt("RoastTicks") / 10;
			this.fillGradient(matrices, x, y + 2, x + progress, y + 4, 0x99000000, 0x99000000);
		}
	}
}
