package doctor4t.astronomical.mixin;

import com.sammy.lodestone.systems.rendering.VFXBuilders;
import doctor4t.astronomical.client.AstronomicalClient;
import doctor4t.astronomical.client.render.world.AstraWorldVFXBuilder;
import doctor4t.astronomical.common.item.NanoAstralObjectItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class AstralObjectItemRendererMixin {
	@Unique
	VFXBuilders.WorldVFXBuilder builder = new AstraWorldVFXBuilder().setPosColorTexLightmapDefaultFormat();

	@Shadow
	public abstract void renderItem(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model);

	@Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At("HEAD"))

	public void renderItem(
		ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light, int overlay, BakedModel model, CallbackInfo ci
	) {
		if (stack.getItem() instanceof NanoAstralObjectItem) {
			matrices.push();

			float scale = .25f;
			if (renderMode == ModelTransformation.Mode.GROUND) {
				scale = .15f;
				matrices.translate(0, .11, 0);
			} else if (renderMode == ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND) {
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-45f));
				matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90f));
				matrices.translate(.2, .10, -.06);
				scale = .17f;
			} else if (renderMode == ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND) {
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-45f));
				matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90f));
				matrices.translate(-.2, .10, -.06);
				scale = .17f;
			} else if (renderMode == ModelTransformation.Mode.FIXED) {
				matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-180f));
				matrices.translate(0,-.03,0);
				scale = .28f;
			} else if (renderMode == ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND || renderMode == ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND) {
				scale = .15f;
				matrices.translate(0, .185, .07);
			}
			float time = ((float) (MinecraftClient.getInstance().world.getTime() % 2400000L) + MinecraftClient.getInstance().getTickDelta());

			matrices.scale(1f, 1, 0.01f);
			matrices.scale(scale, scale, scale);

			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(45f));
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(time));
			AstronomicalClient.renderAstralObject(matrices, vertexConsumerProvider, this.builder, stack, 20, time, false);
			matrices.pop();
		}
	}

}
