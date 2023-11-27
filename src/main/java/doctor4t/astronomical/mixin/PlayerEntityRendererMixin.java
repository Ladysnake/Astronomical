package doctor4t.astronomical.mixin;

import doctor4t.astronomical.cca.AstraCardinalComponents;
import doctor4t.astronomical.common.item.MarshmallowStickItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
	@Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
	private static void getArmPose(@NotNull AbstractClientPlayerEntity abstractClientPlayerEntity, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
		var stackInHand = abstractClientPlayerEntity.getStackInHand(hand);
		var component = AstraCardinalComponents.HOLDING.get(abstractClientPlayerEntity);
		if (stackInHand.getItem() instanceof MarshmallowStickItem && component.isHolding()) {
			cir.setReturnValue(BipedEntityModel.ArmPose.BLOCK);
		}
	}
}
