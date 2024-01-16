package doctor4t.astronomical.common.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import doctor4t.astronomical.client.AstronomicalClient;
import doctor4t.astronomical.client.render.world.AstraWorldVFXBuilder;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.init.ModItems;
import doctor4t.astronomical.common.item.NanoAstralObjectItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Axis;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;

import java.util.function.Consumer;
import java.util.function.Function;

public class PlanetColorScreen extends HandledScreen<PlanetColorScreenHandler> {
	private static final Identifier TEXTURE = Astronomical.id("textures/gui/color_change.png");
	public static final Identifier ASTRAL_WIDGETS_TEXTURE = Astronomical.id("textures/gui/astral_widgets.png");
	VFXBuilders.WorldVFXBuilder builder = new AstraWorldVFXBuilder().setPosColorTexLightmapDefaultFormat();
	private AstralSlider red1Slider;
	private AstralSlider red2Slider;
	private AstralSlider green1Slider;
	private AstralSlider green2Slider;
	private AstralSlider blue1Slider;
	private AstralSlider blue2Slider;
	private int color1;
	private int color2;
	private ItemStack retItemStack;

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
		this.playerInventoryTitleY = -999;
		this.addSliders();

		ItemStack stack = MinecraftClient.getInstance().player.getMainHandStack();
		if (!stack.isOf(ModItems.NANO_PLANET)) {
			stack = MinecraftClient.getInstance().player.getOffHandStack();
			if (!stack.isOf(ModItems.NANO_PLANET)) {
				return;
			}
		}

		retItemStack = stack;
		this.color1 = retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color1");
		this.color2 = retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color2");
		refreshSliders();
	}

	public void addSliders() {
		int RENDER_WIDTH = 0;
		int RENDER_HEIGHT = 0;
		int offsetX1 = -59;
		int offsetX2 = 5;
		int offsetY = -81;

		this.red1Slider = this.addSlider(0xFF7A8B, this.width / 2 + RENDER_WIDTH / 2 + offsetX1, this.height / 2 - RENDER_HEIGHT / 2 + offsetY, 0, 255, (color1) & 0xFF, (m) -> Text.literal("%.0f".formatted(m)), (r) -> {
			color1 &= 0xFF00FFFF;
			color1 |= ((r & 0xFF) << 16);
			this.refreshSliders();
		});
		this.green1Slider = this.addSlider(0x86FFAC, this.width / 2 + RENDER_WIDTH / 2 + offsetX1, this.height / 2 - RENDER_HEIGHT / 2 + offsetY + 20, 0, 255, (color1 >> 8) & 0xFF, (m) -> Text.literal("%.0f".formatted(m)), (g) -> {
			color1 &= 0xFFFF00FF;
			color1 |= ((g & 0xFF) << 8);
			this.refreshSliders();
		});
		this.blue1Slider = this.addSlider(0x86E8FF, this.width / 2 + RENDER_WIDTH / 2 + offsetX1, this.height / 2 - RENDER_HEIGHT / 2 + offsetY + 40, 0, 255, (color1 >> 16) & 0xFF, (m) -> Text.literal("%.0f".formatted(m)), (b) -> {
			color1 &= 0xFFFFFF00;
			color1 |= (b & 0xFF);
			this.refreshSliders();
		});

		this.red2Slider = this.addSlider(0xFF7A8B, this.width / 2 + RENDER_WIDTH / 2 + offsetX2, this.height / 2 - RENDER_HEIGHT / 2 + offsetY, 0, 255, (color2) & 0xFF, (m) -> Text.literal("%.0f".formatted(m)), (r) -> {
			color2 &= 0xFF00FFFF;
			color2 |= ((r & 0xFF) << 16);
			this.refreshSliders();
		});
		this.green2Slider = this.addSlider(0x86FFAC, this.width / 2 + RENDER_WIDTH / 2 + offsetX2, this.height / 2 - RENDER_HEIGHT / 2 + offsetY + 20, 0, 255, (color2 >> 8) & 0xFF, (m) -> Text.literal("%.0f".formatted(m)), (g) -> {
			color2 &= 0xFFFF00FF;
			color2 |= ((g & 0xFF) << 8);
			this.refreshSliders();
		});
		this.blue2Slider = this.addSlider(0x86E8FF, this.width / 2 + RENDER_WIDTH / 2 + offsetX2, this.height / 2 - RENDER_HEIGHT / 2 + offsetY + 40, 0, 255, (color2 >> 16) & 0xFF, (m) -> Text.literal("%.0f".formatted(m)), (b) -> {
			color2 &= 0xFFFFFF00;
			color2 |= (b & 0xFF);
			this.refreshSliders();
		});
	}

	private @NotNull AstralSlider addSlider(int textColor, int x, int y, double min, double max, double value, Function<Double, Text> message, @NotNull Consumer<Integer> consumer) {
		var floatVal = (value - min) / (max - min);
		var slider = new AstralSlider(this, textColor, x, y, 54, 20, floatVal, (d) -> this.refreshSliders()) {
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
		this.red1Slider.setValue(((color1 >> 16) & 0xFF) / 255f);
		this.green1Slider.setValue(((color1 >> 8) & 0xFF) / 255f);
		this.blue1Slider.setValue((color1 & 0xFF) / 255f);

		this.red2Slider.setValue(((color2 >> 16) & 0xFF) / 255f);
		this.green2Slider.setValue(((color2 >> 8) & 0xFF) / 255f);
		this.blue2Slider.setValue((color2 & 0xFF) / 255f);

		syncColors(color1, color2);
	}

	private void syncColors(int color1, int color2) {
		var buf = PacketByteBufs.create();
		buf.writeInt(color1);
		buf.writeInt(color2);
		ClientPlayNetworking.send(Astronomical.PLANET_COLOR_CHANGE_PACKET, buf);
	}

	@Override
	protected void drawBackground(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		var x = (this.width - this.backgroundWidth) / 2;
		var y = (this.height - this.backgroundHeight) / 2;

		graphics.drawTexture(TEXTURE, x, y, -400, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
		drawMouseoverTooltip(graphics, mouseX, mouseY);

		retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("color1", color1);
		retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("color2", color2);
		retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("size", 1);
		renderStar(retItemStack, x + this.backgroundWidth / 2 - 8, y + this.backgroundHeight / 2 - 8);
	}

	protected void renderStar(ItemStack stack, int x, int y) {
		RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		MatrixStack matrixStack = RenderSystem.getModelViewStack();
		matrixStack.push();
		matrixStack.translate(x, y, 100.0F + 200f);
		matrixStack.translate(8.0, 8.0, 0.0);
		matrixStack.scale(1.0F, -1.0F, 1.0F);
		matrixStack.scale(32.0F, 32.0F, 32.0F);
		RenderSystem.applyModelViewMatrix();
		MatrixStack matrices = new MatrixStack();
		VertexConsumerProvider.Immediate vertexConsumerProvider = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

		if (stack.getItem() instanceof NanoAstralObjectItem) {
			matrices.push();

			float scale = 1.2f;
			float time = ((float) (MinecraftClient.getInstance().world.getTime() % 2400000L) + MinecraftClient.getInstance().getTickDelta());

			matrices.scale(1f, 1, 0.1f);
			matrices.scale(scale, scale, scale);
			matrices.translate(0f, -.925f, 0f);

			matrices.multiply(Axis.X_POSITIVE.rotationDegrees(15f));
			matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(time));

			AstronomicalClient.renderAstralObject(matrices, vertexConsumerProvider, this.builder, stack, 20, time, false);
			matrices.pop();
		}
		vertexConsumerProvider.draw();
		RenderSystem.enableDepthTest();

		matrixStack.pop();
		RenderSystem.applyModelViewMatrix();
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.getFocused() != null) {
			return this.isDragging() && button == 0 && this.getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Environment(EnvType.CLIENT)
	private static class AstralSlider extends SliderWidget {
		private final int backgroundHeight;
		private final Consumer<Double> syncConsumer;
		private final int textColor;

		private AstralSlider(PlanetColorScreen screen, int textColor, int x, int y, int width, int height, double value, Consumer<Double> syncConsumer) {
			super(x, y, width, 20, Text.empty(), value);
			this.backgroundHeight = height;
			this.syncConsumer = syncConsumer;
			this.textColor = textColor;
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
		public void drawWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
			var minecraftClient = MinecraftClient.getInstance();
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
			// todo what
//			var i = this.getYImage(this.isHoveredOrFocused());
			int i = 0;
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			graphics.drawTexture(PlanetColorScreen.ASTRAL_WIDGETS_TEXTURE, this.getX(), this.getY() + this.height / 2 - this.backgroundHeight / 2, 0, 46 + i * 20, this.width / 2, this.backgroundHeight - 1);
			graphics.drawTexture(PlanetColorScreen.ASTRAL_WIDGETS_TEXTURE, this.getX(), this.getY() + this.height / 2 - this.backgroundHeight / 2 + this.backgroundHeight - 1, 0, 46 + i * 20, this.width / 2, 1);
			graphics.drawTexture(PlanetColorScreen.ASTRAL_WIDGETS_TEXTURE, this.getX() + this.width / 2, this.getY() + this.height / 2 - this.backgroundHeight / 2, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.backgroundHeight - 1);
			graphics.drawTexture(PlanetColorScreen.ASTRAL_WIDGETS_TEXTURE, this.getX() + this.width / 2, this.getY() + this.height / 2 - this.backgroundHeight / 2 + this.backgroundHeight - 1, 200 - this.width / 2, 46 + i * 20, this.width / 2, 1);

			// background
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			i = (this.isHoveredOrFocused() ? 2 : 1) * 20;
			graphics.drawTexture(PlanetColorScreen.ASTRAL_WIDGETS_TEXTURE, this.getX() + (int) (this.value * (double) (this.width - 8)), this.getY(), 0, 46 + i, 4, 20);
			graphics.drawTexture(PlanetColorScreen.ASTRAL_WIDGETS_TEXTURE, this.getX() + (int) (this.value * (double) (this.width - 8)) + 4, this.getY(), 4, 46 + i, 4, 20);

			graphics.drawCenteredShadowedText(minecraftClient.textRenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, this.textColor | MathHelper.ceil(this.alpha * 255.0F) << 24);
		}
	}
}
