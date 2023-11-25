package doctor4t.astronomical.common.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.util.DoubleHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class AstralDisplayScreen extends HandledScreen<AstralDisplayScreenHandler> {
	private static final Identifier TEXTURE = Astronomical.id("textures/gui/astraldisplay.png");
	private static final Text YLEVEL_TEXT = Text.translatable("screen.astronomical.astraldisplay.ylevel");
	private static final Text ROTSPEED_TEXT = Text.translatable("screen.astronomical.astraldisplay.rotspeed");
	private static final Text SPIN_TEXT = Text.translatable("screen.astronomical.astraldisplay.spin");
	public final DoubleHolder yLevel = new DoubleHolder(0.5);
	public final DoubleHolder rotSpeed = new DoubleHolder(0.5);
	public final DoubleHolder spin = new DoubleHolder(0.5);

	public AstralDisplayScreen(AstralDisplayScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	private void updateSliders() {
		ClientPlayNetworking.send(Astronomical.id("astral_display"), this.sliderBuf());
	}

	private @NotNull PacketByteBuf sliderBuf() {
		var buf = PacketByteBufs.create();
		buf.writeDouble(this.yLevel.get());
		buf.writeDouble(this.rotSpeed.get());
		buf.writeDouble(this.spin.get());
		return buf;
	}

	@Override
	protected void init() {
		super.init();
		this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
		this.backgroundWidth = 176;
		this.backgroundHeight = 166;
		this.playerInventoryTitleY = this.backgroundHeight - 94;
		this.yLevel.silentSet(this.handler.yLevel);
		var yLevelSlider = new AstralSlider(this, this.x + 10, this.y + 47, 46, 6, this.yLevel);
		this.addDrawableChild(yLevelSlider);
		this.rotSpeed.silentSet(this.handler.rotSpeed);
		var rotSpeedSlider = new AstralSlider(this, this.x + 65, this.y + 47, 46, 6, this.rotSpeed);
		this.addDrawableChild(rotSpeedSlider);
		this.spin.silentSet(this.handler.spin);
		var spinSlider = new AstralSlider(this, this.x + 120, this.y + 47, 46, 6, this.spin);
		this.addDrawableChild(spinSlider);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.setShaderTexture(0, TEXTURE);
		this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
	}

	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		super.drawForeground(matrices, mouseX, mouseY);
		this.textRenderer.draw(matrices, YLEVEL_TEXT, 10 + 26 - this.textRenderer.getWidth(YLEVEL_TEXT) / 2f, (float)this.titleY + 31, 0x404040);
		this.textRenderer.draw(matrices, ROTSPEED_TEXT, 65 + 26 - this.textRenderer.getWidth(ROTSPEED_TEXT) / 2f, (float)this.titleY + 31, 0x404040);
		this.textRenderer.draw(matrices, SPIN_TEXT, 120 + 26 - this.textRenderer.getWidth(SPIN_TEXT) / 2f, (float)this.titleY + 31, 0x404040);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.getFocused() != null) {
			return this.isDragging() && button == 0 && this.getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Environment(EnvType.CLIENT)
	private static final class AstralSlider extends SliderWidget {
		private final AstralDisplayScreen screen;
		private final DoubleHolder heldValue;
		private final int backgroundHeight;

		private AstralSlider(AstralDisplayScreen screen, int x, int y, int width, int height, @NotNull DoubleHolder heldValue) {
			super(x, y, width, 20, Text.empty(), heldValue.get());
			this.screen = screen;
			this.backgroundHeight = height;
			this.heldValue = heldValue;
		}

		@Override
		protected void updateMessage() {}

		@Override
		protected void applyValue() {
			this.heldValue.set(this.value);
		}

		@Override
		public void onRelease(double mouseX, double mouseY) {
            this.screen.updateSliders();
			super.onRelease(mouseX, mouseY);
		}

		@Override
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			var minecraftClient = MinecraftClient.getInstance();
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
			var i = this.getYImage(this.isHoveredOrFocused());
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			this.drawTexture(matrices, this.x, this.y + this.height / 2 - this.backgroundHeight / 2, 0, 46 + i * 20, this.width / 2, this.backgroundHeight - 1);
			this.drawTexture(matrices, this.x, this.y + this.height / 2 - this.backgroundHeight / 2 + this.backgroundHeight - 1, 0, 46 + i * 20, this.width / 2, 1);
			this.drawTexture(matrices, this.x + this.width / 2, this.y + this.height / 2 - this.backgroundHeight / 2, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.backgroundHeight - 1);
			this.drawTexture(matrices, this.x + this.width / 2, this.y + this.height / 2 - this.backgroundHeight / 2 + this.backgroundHeight - 1, 200 - this.width / 2, 46 + i * 20, this.width / 2, 1);
			this.renderBackground(matrices, minecraftClient, mouseX, mouseY);
		}
	}
}
