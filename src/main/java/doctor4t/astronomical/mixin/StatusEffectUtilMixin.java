package doctor4t.astronomical.mixin;

import doctor4t.astronomical.common.Astronomical;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.text.component.TranslatableComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(StatusEffectUtil.class)
public class StatusEffectUtilMixin {
	@Inject(method = "durationToString", at = @At("HEAD"), cancellable = true)
	private static void astronomical$untilMorning(@NotNull StatusEffectInstance effect, float multiplier, CallbackInfoReturnable<String> cir) {
		if (effect.getEffectType() == Astronomical.STARGAZING_EFFECT) {
			var stringBuilder = new StringBuilder();
			new TranslatableComponent("effect.astronomical.stargazing.time").visit(string -> {
				stringBuilder.append(string);
				return Optional.empty();
			});
			cir.setReturnValue(stringBuilder.toString());
		}
	}
}
