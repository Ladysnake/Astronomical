package doctor4t.astronomical.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.item.MarshmallowStickItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
	@Shadow @Final private MinecraftClient client;
	@Shadow private int scaledWidth;
	@Shadow private int scaledHeight;

	@Unique private static final Identifier MARSHMALLOW_TEXTURE = Astronomical.id("textures/gui/marshmallow.png");

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
			var width = MarshmallowStickItem.CookState.values()[MarshmallowStickItem.CookState.values().length - 1].cookTime / 10 + 6;
			var x = this.scaledWidth / 2 - width / 2;
			var y = this.scaledHeight / 2 + 9;
			var progress = player.getMainHandStack().getOrCreateNbt().getInt("RoastTicks") / 10;
			for (var i = 0; i < width; i++) {
				var progressed = i <= progress
				RenderSystem.setShaderTexture(0, MARSHMALLOW_TEXTURE);
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.enableBlend();
//				var state = getStateAtI(i);
//				var rgb = new float[] { state.color >> 16 & 255, state.color >> 8 & 255, state.color & 255, state.color >> 24 & 255 };
//				RenderSystem.setShaderColor(rgb[0] / 255, rgb[1] / 255, rgb[2] / 255, 1.0f);
				RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
				var u = i <= 4 ? i : i > width - 5 ? 7 + i - width - 5 : 5;
//				this.fillGradient(matrices, x + i, y, x + i + 1, y + 5, progressed ? state.color : 0xFF000000, progressed ? state.color : 0xFF000000);
				this.drawTexture(matrices, x + i, y, 1, progressed ? 5 : 0, 2, 5);
				RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
			}
		}
	}

	@Unique
	private static MarshmallowStickItem.CookState getStateAtI(int i) {
		var states = MarshmallowStickItem.CookState.values();
		var state = states[states.length - 1];
        for (var cookState : states) {
            if (cookState.cookTime / 10 >= i) {
                return cookState;
            }
        }
		return state;
	}
}
