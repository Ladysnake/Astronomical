package doctor4t.astronomical.common.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sammy.lodestone.systems.rendering.VFXBuilders;
import doctor4t.astronomical.client.AstronomicalClient;
import doctor4t.astronomical.client.render.world.AstraWorldVFXBuilder;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.init.ModItems;
import doctor4t.astronomical.common.item.NanoAstralObjectItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import java.util.function.Consumer;
import java.util.function.Function;

public class RingColorScreen extends HandledScreen<RingColorScreenHandler> {
	private static final Identifier TEXTURE = Astronomical.id("textures/gui/color_change.png");
	private static final Identifier WIDGETS_TEXTURE = new Identifier("minecraft", "textures/gui/widgets.png");
	VFXBuilders.WorldVFXBuilder builder = new AstraWorldVFXBuilder().setPosColorTexLightmapDefaultFormat();
	private SliderWidget redSlider;
	private SliderWidget greenSlider;
	private SliderWidget blueSlider;
	private int color;
	private ItemStack retItemStack;

	public RingColorScreen(RingColorScreenHandler handler, PlayerInventory inventory, Text title) {
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
		if (!stack.isOf(ModItems.NANO_RING)) {
			stack = MinecraftClient.getInstance().player.getOffHandStack();
			if (!stack.isOf(ModItems.NANO_RING)) {
				return;
			}
		}

		retItemStack = stack;
		this.color = retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color");
		refreshSliders();
	}

	public void addSliders() {
		int RENDER_WIDTH = 0;
		int RENDER_HEIGHT = 0;
		int offsetX = -27;
		int offsetY = -81;

		this.redSlider = this.addSlider(this.width / 2 + RENDER_WIDTH / 2 + offsetX, this.height / 2 - RENDER_HEIGHT / 2 + offsetY, 0, 255, (color) & 0xFF, (m) -> Text.literal("Red: %.0f".formatted(m)), (r) -> {
			color &= 0xFF00FFFF;
			color |= ((r & 0xFF) << 16);
			this.refreshSliders();
		});
		this.greenSlider = this.addSlider(this.width / 2 + RENDER_WIDTH / 2 + offsetX, this.height / 2 - RENDER_HEIGHT / 2 + offsetY + 20, 0, 255, (color >> 8) & 0xFF, (m) -> Text.literal("Green: %.0f".formatted(m)), (g) -> {
			color &= 0xFFFF00FF;
			color |= ((g & 0xFF) << 8);
			this.refreshSliders();
		});
		this.blueSlider = this.addSlider(this.width / 2 + RENDER_WIDTH / 2 + offsetX, this.height / 2 - RENDER_HEIGHT / 2 + offsetY + 40, 0, 255, (color >> 16) & 0xFF, (m) -> Text.literal("Blue: %.0f".formatted(m)), (b) -> {
			color &= 0xFFFFFF00;
			color |= (b & 0xFF);
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
		this.redSlider.setValue(((color >> 16) & 0xFF) / 255f);
		this.greenSlider.setValue(((color >> 8) & 0xFF) / 255f);
		this.blueSlider.setValue((color & 0xFF) / 255f);

		syncColors(color);
	}

	private void syncColors(int color) {
		var buf = PacketByteBufs.create();
		buf.writeInt(color);
		ClientPlayNetworking.send(Astronomical.RING_COLOR_CHANGE_PACKET, buf);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE);
		var x = (this.width - this.backgroundWidth) / 2;
		var y = (this.height - this.backgroundHeight) / 2;
		matrices.push();
		//matrices.translate(0f,0f,-450f);
		drawTexture(matrices, x, y, -400, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
		matrices.pop();

		retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("color", color);
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

			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90f));
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(time));

			AstronomicalClient.renderAstralObject(matrices, vertexConsumerProvider, this.builder, stack, 20, time, false);
			matrices.pop();
		}
		vertexConsumerProvider.draw();
		RenderSystem.enableDepthTest();

		matrixStack.pop();
		RenderSystem.applyModelViewMatrix();
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

		private AstralSlider(RingColorScreen screen, int x, int y, int width, int height, double value, Consumer<Double> syncConsumer) {
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
			RenderSystem.setShaderTexture(0, RingColorScreen.WIDGETS_TEXTURE);
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
