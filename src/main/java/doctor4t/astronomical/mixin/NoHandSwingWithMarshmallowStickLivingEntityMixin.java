package doctor4t.astronomical.mixin;

import doctor4t.astronomical.common.item.MarshmallowStickItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class NoHandSwingWithMarshmallowStickLivingEntityMixin {
	@Shadow
	public abstract ItemStack getStackInHand(Hand hand);

	@Inject(method = "swingHand(Lnet/minecraft/util/Hand;Z)V", at = @At("HEAD"), cancellable = true)
	private void astronomical$swingHand(Hand hand, boolean fromServerPlayer, CallbackInfo ci) {
		var stack = this.getStackInHand(hand);
		if (stack.getItem() instanceof MarshmallowStickItem) {
			if (!(((LivingEntity) Object.class.cast(this)).getUuid().equals(MinecraftClient.getInstance().player.getUuid()) && MinecraftClient.getInstance().options.getPerspective().isFirstPerson())) {
				ci.cancel();
			}
		}
	}
}
