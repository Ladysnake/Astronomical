package doctor4t.astronomical.mixin;

import doctor4t.astronomical.client.AstronomicalClient;
import doctor4t.astronomical.client.render.world.AstraWorldVFXBuilder;
import doctor4t.astronomical.common.init.ModItems;
import doctor4t.astronomical.common.item.NanoAstralObjectItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Axis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;

@Mixin(ItemRenderer.class)
public abstract class AstralObjectItemRendererMixin {
	@Unique
	VFXBuilders.WorldVFXBuilder builder = new AstraWorldVFXBuilder().setPosColorTexLightmapDefaultFormat();
	
	@Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At("HEAD"))
	public void renderItem(
			ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci
	) {
		if (stack.getItem() instanceof NanoAstralObjectItem) {
			matrices.push();

			boolean delayRender = false;

			float scale = .25f;
			if (renderMode == ModelTransformationMode.GROUND) {
				scale = .13f;
				matrices.translate(0, .13, .0);
			} else if (renderMode == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND) {
				matrices.multiply(Axis.X_POSITIVE.rotationDegrees(-45f));
				matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(-90f));
				matrices.translate(.2, .10, -(stack.isOf(ModItems.NANO_RING) ? .06 : .1));
				scale = .17f;
			} else if (renderMode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND) {
				matrices.multiply(Axis.X_POSITIVE.rotationDegrees(-45f));
				matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(90f));
				matrices.translate(-.2, .10, -(stack.isOf(ModItems.NANO_RING) ? .06 : .1));
				scale = .17f;
			} else if (renderMode == ModelTransformationMode.FIXED) {
				matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(-180f));
				matrices.translate(0, -.03, -.05);
				if (stack.isOf(ModItems.NANO_RING))
					delayRender = true;
				scale = .28f;
			} else if (renderMode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND || renderMode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND) {
				scale = .14f;
				matrices.translate(0, .2, .07);
			}
			float time = ((float) (MinecraftClient.getInstance().world.getTime() % 2400000L) + MinecraftClient.getInstance().getTickDelta());

			matrices.scale(1f, 1, 0.01f);
			matrices.scale(scale, scale, scale);

			matrices.multiply(Axis.X_POSITIVE.rotationDegrees(stack.isOf(ModItems.NANO_RING) ? 90f : 15f));
			matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(time));
			AstronomicalClient.renderAstralObject(matrices, vertexConsumers, this.builder, stack, 20, time, delayRender);
			matrices.pop();
		}
	}
}
