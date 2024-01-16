package doctor4t.astronomical.common.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.block.AstralDisplayBlock;
import doctor4t.astronomical.common.block.entity.AstralDisplayBlockEntity;
import doctor4t.astronomical.common.util.ScaledDouble;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.function.Consumer;

public class AstralDisplayScreen extends HandledScreen<AstralDisplayScreenHandler> {
	public static final Identifier ASTRAL_WIDGETS_TEXTURE = Astronomical.id("textures/gui/astral_widgets.png");
	private static final Identifier TEXTURE = Astronomical.id("textures/gui/astral_display.png");
	private AstralSlider topSlider;
	private AstralSlider bottomSlider;

	public AstralDisplayScreen(AstralDisplayScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title.copy().setStyle(Style.EMPTY.withColor(0xD5B271)));
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

		if (this.topSlider == null) {
			MinecraftClient mC = MinecraftClient.getInstance();
			if (mC.world != null && mC.world.getBlockState(this.handler.entity.getPos()).get(AstralDisplayBlock.FACING).equals(Direction.UP)) {
				this.topSlider = new AstralSlider(this.handler.entity().yLevel, this.x + 8, this.y + 35, 161, 6, this.handler.entity().yLevel.getValue(), (d) -> this.syncSlider(Astronomical.Y_LEVEL_PACKET, d), 1);
				this.addDrawableChild(this.topSlider);
			} else {
				this.topSlider = new AstralSlider(this.handler.entity().rotSpeed, this.x + 8, this.y + 35, 161, 6, this.handler.entity().rotSpeed.getValue(), (d) -> this.syncSlider(Astronomical.ROT_SPEED_PACKET, d), 2);
				this.addDrawableChild(this.topSlider);
			}
		}

		if (this.bottomSlider == null) {
			this.bottomSlider = new AstralSlider(this.handler.entity().spin, this.x + 8, this.y + 58, 161, 6, this.handler.entity().spin.getValue(), (d) -> this.syncSlider(Astronomical.SPIN_PACKET, d), 0);
			this.addDrawableChild(this.bottomSlider);
		}
	}

	private void syncSlider(Identifier packet, double value) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeDouble(value);
		ClientPlayNetworking.send(packet, buf);
	}

	@Override
	protected void drawBackground(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		int x = (this.width - this.backgroundWidth) / 2;
		int y = (this.height - this.backgroundHeight) / 2;
		graphics.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
		this.drawMouseoverTooltip(graphics, mouseX, mouseY);
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
		private final byte type;
		private final Consumer<Double> syncConsumer;
		private final ScaledDouble scaledDouble;

		private AstralSlider(ScaledDouble scaledDouble, int x, int y, int width, int height, double value, Consumer<Double> syncConsumer, int type) {
			super(x, y, width, 20, Text.empty(), value);
			this.type = (byte) MathHelper.clamp(type, 0, 7);
			this.syncConsumer = syncConsumer;
			this.scaledDouble = scaledDouble;
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
		public Text getMessage() {
			DecimalFormat df = new DecimalFormat("#.##");
			df.setRoundingMode(RoundingMode.CEILING);
			return Text.literal(df.format(this.scaledDouble.getScaledValue()));
		}

		@Override
		public void drawWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
//			int i = this.getYImage(this.isHoveredOrFocused());
//			RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
//			this.drawTexture(matrices, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
//			this.drawTexture(matrices, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
			int i = this.isHoveredOrFocused() ? 1 : 0;
			graphics.drawTexture(ASTRAL_WIDGETS_TEXTURE, this.getX() + (int) (this.value * (double) (this.width - 21)), this.getY(), type * 20, i * 20, 20, 20);
			var minecraftClient = MinecraftClient.getInstance();
			graphics.drawCenteredShadowedText(minecraftClient.textRenderer, getMessage(), getX() + width / 2, getY() + (height - 8) / 2, 0xD5B271 | MathHelper.ceil(this.alpha * 255.0F) << 24);
		}
	}
}
