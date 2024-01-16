package doctor4t.astronomical.mixin;

import doctor4t.astronomical.common.init.ModStatusEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectUtil.class)
public class StatusEffectUtilMixin {
	@Inject(method = "durationToString", at = @At("HEAD"), cancellable = true)
	private static void astronomical$untilMorning(@NotNull StatusEffectInstance effect, float multiplier, CallbackInfoReturnable<Text> cir) {
		if (effect.getEffectType() == ModStatusEffects.STARGAZING || effect.getEffectType() == ModStatusEffects.STARFALL) {
			cir.setReturnValue(Text.translatable("effect.astronomical.stargazing.time"));
		}
	}
}
