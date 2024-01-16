package doctor4t.astronomical.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.item.MarshmallowStickItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
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
public abstract class InGameHudMixin {
	@Unique
	private static final Identifier MARSHMALLOW_TEXTURE = Astronomical.id("textures/gui/marshmallow.png");
	@Shadow
	@Final
	private MinecraftClient client;
	@Shadow
	private int scaledWidth;
	@Shadow
	private int scaledHeight;

	@Unique
	private static MarshmallowStickItem.CookState getStateAtI(int i) {
		var states = MarshmallowStickItem.CookState.values();
		var state = states[states.length - 1];
		for (var cookState : states) {
			if (cookState.next().cookTime / 10 >= i) {
				return cookState;
			}
		}
		return state;
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/gui/GuiGraphics;)V", shift = At.Shift.AFTER))
	private void astronomical$renderCrosshair(GuiGraphics graphics, float tickDelta, CallbackInfo ci) {
		var gameOptions = this.client.options;
		if (!gameOptions.getPerspective().isFirstPerson() || this.client.interactionManager == null || this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
			return;
		}
		var player = this.client.player;
		if (player == null || !(player.getMainHandStack().getItem() instanceof MarshmallowStickItem)) {
			return;
		}
		if (player.astronomical$isHoldingAttack() && player.isCreative()) {
			var width = MarshmallowStickItem.CookState.values()[MarshmallowStickItem.CookState.values().length - 1].cookTime / 10 + 6;
			var x = this.scaledWidth / 2 - width / 2;
			var y = this.scaledHeight / 2 + 9;
			var progress = player.getMainHandStack().getOrCreateNbt().getInt("RoastTicks") / 10 + 1;
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			for (var i = 0; i < width; i++) {
				var progressed = i <= progress;
				var state = getStateAtI(i);
				var rgb = new float[]{state.color >> 16 & 255, state.color >> 8 & 255, state.color & 255, state.color >> 24 & 255};
				RenderSystem.setShaderColor(rgb[0] / 255, rgb[1] / 255, rgb[2] / 255, 1.0f);
				var u = i < 4 ? i : i > width - 5 ? 7 + i - width + 4 : 5;
				graphics.drawTexture(MARSHMALLOW_TEXTURE, x + i, y, u, progressed ? 5 : 0, 2, 5, 16, 16);
			}
			RenderSystem.disableBlend();
		}
	}
}
