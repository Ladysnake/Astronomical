package doctor4t.astronomical.common.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.block.entity.AstralDisplayBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import java.util.function.Consumer;

public class AstralDisplayScreen extends HandledScreen<AstralDisplayScreenHandler> {
	public static final Identifier ASTRAL_WIDGETS_TEXTURE = new Identifier("textures/gui/astral_widgets.png");
	private static final Identifier TEXTURE = Astronomical.id("textures/gui/astral_display.png");
	private static final Text YLEVEL_TEXT = Text.translatable("screen.astronomical.astraldisplay.ylevel");
	private static final Text ROTSPEED_TEXT = Text.translatable("screen.astronomical.astraldisplay.rotspeed");
	private static final Text SPIN_TEXT = Text.translatable("screen.astronomical.astraldisplay.spin");
	private AstralSlider yLevelSlider;
	private AstralSlider rotSpeedSlider;
	private AstralSlider spinSlider;

	public AstralDisplayScreen(AstralDisplayScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void init() {
		super.init();
		this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
		this.titleY -= 9;
		this.backgroundWidth = 176;
		this.backgroundHeight = 184;
		this.playerInventoryTitleY = this.backgroundHeight - 102;
		if (!(this.handler.entity() instanceof AstralDisplayBlockEntity)) {
			return;
		}
		this.addSliders();
	}

	public void addSliders() {
		if (this.handler.entity() == null) return;
//		if (this.yLevelSlider == null) {
//			this.yLevelSlider = new AstralSlider(this, this.x + 8, this.y + 31, 161, 6, this.handler.entity().yLevel, (d) -> this.syncSlider(Astronomical.Y_LEVEL_PACKET, d));
//			this.addDrawableChild(this.yLevelSlider);
//		}
		if (this.rotSpeedSlider == null) {
			this.rotSpeedSlider = new AstralSlider(this, this.x + 8, this.y + 31, 161, 6, this.handler.entity().rotSpeed.getValue(), (d) -> this.syncSlider(Astronomical.ROT_SPEED_PACKET, d));
			this.addDrawableChild(this.rotSpeedSlider);
		}
		if (this.spinSlider == null) {
			this.spinSlider = new AstralSlider(this, this.x + 8, this.y + 55, 161, 6, this.handler.entity().spin.getValue(), (d) -> this.syncSlider(Astronomical.SPIN_PACKET, d));
			this.addDrawableChild(this.spinSlider);
		}
	}

	private void syncSlider(Identifier packet, double value) {
		var buf = PacketByteBufs.create();
		buf.writeDouble(value);
		ClientPlayNetworking.send(packet, buf);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE);
		var x = (this.width - this.backgroundWidth) / 2;
		var y = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
//		this.textRenderer.draw(matrices, YLEVEL_TEXT, x + 10 + 26 - this.textRenderer.getWidth(YLEVEL_TEXT) / 2f, y + (float) this.titleY + 31, 0x404040);
//		this.textRenderer.draw(matrices, ROTSPEED_TEXT, x + 65 + 26 - this.textRenderer.getWidth(ROTSPEED_TEXT) / 2f, y + (float) this.titleY + 31, 0x404040);
//		this.textRenderer.draw(matrices, SPIN_TEXT, x + 120 + 26 - this.textRenderer.getWidth(SPIN_TEXT) / 2f, y + (float) this.titleY + 31, 0x404040);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		super.drawForeground(matrices, mouseX, mouseY);
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
		private final int backgroundHeight;
		private final Consumer<Double> syncConsumer;

		private AstralSlider(AstralDisplayScreen screen, int x, int y, int width, int height, double value, Consumer<Double> syncConsumer) {
			super(x, y, width, 20, Text.empty(), value);
			this.backgroundHeight = height;
			this.syncConsumer = syncConsumer;
		}

		@Override
		protected void updateMessage() {
		}

		@Override
		protected void applyValue() {
			this.syncConsumer.accept(this.value);
		}

		@Override
		public boolean mouseReleased(double mouseX, double mouseY, int button) {
			this.playDownSound(MinecraftClient.getInstance().getSoundManager());
			return super.mouseReleased(mouseX, mouseY, button);
		}

		@Override
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			var minecraftClient = MinecraftClient.getInstance();
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, ASTRAL_WIDGETS_TEXTURE);
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
