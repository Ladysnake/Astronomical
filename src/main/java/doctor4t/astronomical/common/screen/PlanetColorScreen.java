package doctor4t.astronomical.common.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sammy.lodestone.handlers.RenderHandler;
import doctor4t.astronomical.client.AstronomicalClient;
import doctor4t.astronomical.client.render.world.AstraWorldVFXBuilder;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.item.NanoPlanetItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class PlanetColorScreen extends HandledScreen<PlanetColorScreenHandler> {
	private static final Identifier TEXTURE = Astronomical.id("textures/gui/astral_display.png");
	public static final Identifier ASTRAL_WIDGETS_TEXTURE = new Identifier("textures/gui/astral_widgets.png");
	private SliderWidget red1Slider;
	private SliderWidget red2Slider;
	private SliderWidget green1Slider;
	private SliderWidget green2Slider;
	private SliderWidget blue1Slider;
	private SliderWidget blue2Slider;
	private int color1;
	private int color2;

	public PlanetColorScreen(PlanetColorScreenHandler handler, PlayerInventory inventory, Text title) {
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
		this.addSliders();
	}

	public void addSliders() {
		int RENDER_WIDTH = 0;
		int RENDER_HEIGHT = 0;
		int offsetX1 = -80;
		int offsetY1 = -70;
		int offsetX2 = 0;
		int offsetY2 = -70;

		this.red1Slider = this.addSlider(this.width / 2 + RENDER_WIDTH / 2 + offsetX1, this.height / 2 - RENDER_HEIGHT / 2 + offsetY1 + 40, 0, 255, (color1) & 0xFF, (m) -> Text.literal("Red: %.0f".formatted(m)), (r) -> {
			color1 &= 0xFF00FFFF;
			color1 |= ((r & 0xFF) << 16);
			this.refreshSliders();
		});
		this.green1Slider = this.addSlider(this.width / 2 + RENDER_WIDTH / 2 + offsetX1, this.height / 2 - RENDER_HEIGHT / 2 + offsetY1 + 20, 0, 255, (color1 >> 8) & 0xFF, (m) -> Text.literal("Green: %.0f".formatted(m)), (g) -> {
			color1 &= 0xFFFF00FF;
			color1 |= ((g & 0xFF) << 8);
			this.refreshSliders();
		});
		this.blue1Slider = this.addSlider(this.width / 2 + RENDER_WIDTH / 2 + offsetX1, this.height / 2 - RENDER_HEIGHT / 2 + offsetY1, 0, 255, (color1 >> 16) & 0xFF, (m) -> Text.literal("Blue: %.0f".formatted(m)), (b) -> {
			color1 &= 0xFFFFFF00;
			color1 |= (b & 0xFF);
			this.refreshSliders();
		});

		this.red2Slider = this.addSlider(this.width / 2 + RENDER_WIDTH / 2 + offsetX2, this.height / 2 - RENDER_HEIGHT / 2 + offsetY2 + 40, 0, 255, (color2) & 0xFF, (m) -> Text.literal("Red: %.0f".formatted(m)), (r) -> {
			color2 &= 0xFF00FFFF;
			color2 |= ((r & 0xFF) << 16);
			this.refreshSliders();
		});
		this.green2Slider = this.addSlider(this.width / 2 + RENDER_WIDTH / 2 + offsetX2, this.height / 2 - RENDER_HEIGHT / 2 + offsetY2 + 20, 0, 255, (color2 >> 8) & 0xFF, (m) -> Text.literal("Green: %.0f".formatted(m)), (g) -> {
			color2 &= 0xFFFF00FF;
			color2 |= ((g & 0xFF) << 8);
			this.refreshSliders();
		});
		this.blue2Slider = this.addSlider(this.width / 2 + RENDER_WIDTH / 2 + offsetX2, this.height / 2 - RENDER_HEIGHT / 2 + offsetY2, 0, 255, (color2 >> 16) & 0xFF, (m) -> Text.literal("Blue: %.0f".formatted(m)), (b) -> {
			color2 &= 0xFFFFFF00;
			color2 |= (b & 0xFF);
			this.refreshSliders();
		});
	}

	private @NotNull SliderWidget addSlider(int x, int y, double min, double max, double value, Function<Double, Text> message, @NotNull Consumer<Integer> consumer) {
		var floatVal = (value - min) / (max - min);
		var slider = new SliderWidget(x, y, 55, 20, message.apply(value), floatVal) {
			@Override
			protected void updateMessage() {
				this.setMessage(message.apply(this.value * (max - min) + min));
			}

			@Override
			protected void applyValue() {
				consumer.accept((int) (this.value * (max - min) + min));
			}
		};
		this.addDrawableChild(slider);
		return slider;
	}

	private void refreshSliders() {
		this.green1Slider.setValue(((color1 >> 8) & 0xFF) / 255f);
		this.red1Slider.setValue(((color1 >> 16) & 0xFF) / 255f);
		this.blue1Slider.setValue((color1 & 0xFF) / 255f);

		this.green2Slider.setValue(((color2 >> 8) & 0xFF) / 255f);
		this.red2Slider.setValue(((color2 >> 16) & 0xFF) / 255f);
		this.blue2Slider.setValue((color2 & 0xFF) / 255f);
	}

	private void syncSlider(String id, double value) {
		var buf = PacketByteBufs.create();
		buf.writeDouble(value);
		ClientPlayNetworking.send(Astronomical.id(id), buf);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE);
		var x = (this.width - this.backgroundWidth) / 2;
		var y = (this.height - this.backgroundHeight) / 2;
		matrices.push();
		matrices.translate(0f,0f,-450f);
		this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
		matrices.pop();

		AstraWorldVFXBuilder builder = new AstraWorldVFXBuilder();
		RenderLayer renderLayer = AstraWorldVFXBuilder.TEXTURE_ACTUAL_TRIANGLE_TRANSPARENT.applyAndCache(NanoPlanetItem.PlanetTexture.HOME.texture);
		VertexConsumerProvider.Immediate vertexConsumerProvider = RenderHandler.LATE_DELAYED_RENDER;
		int sphereDetail = 20;

		matrices.push();
		matrices.scale(.01f,.01f,.01f);
		matrices.translate(0,0,-6);

		builder.setColor(new Color(color1))
			.setAlpha(1f)
			.renderSphere(
				vertexConsumerProvider.getBuffer(AstronomicalClient.SOLID_BALL),
				matrices,
				1,
				sphereDetail,
				sphereDetail);

		builder.setColor(new Color(color2))
			.setAlpha(1f)
			.renderSphere(
				vertexConsumerProvider.getBuffer(renderLayer),
				matrices,
				1,
				sphereDetail,
				sphereDetail);

		matrices.pop();
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

		private AstralSlider(PlanetColorScreen screen, int x, int y, int width, int height, double value, Consumer<Double> syncConsumer) {
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
