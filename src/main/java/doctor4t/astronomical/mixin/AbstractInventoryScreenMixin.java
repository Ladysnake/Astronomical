package doctor4t.astronomical.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import doctor4t.astronomical.common.Astronomical;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractInventoryScreen.class)
public class AbstractInventoryScreenMixin {
	@WrapOperation(method = "drawStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;literal(Ljava/lang/String;)Lnet/minecraft/text/MutableText;"))
	private MutableText astronomical$untilMorning(String text, Operation<MutableText> original, @Local(ordinal = 0) @NotNull StatusEffectInstance effect) {
		if (effect.getEffectType() == Astronomical.STARGAZING_EFFECT) {
			return Text.translatable("effect.astronomical.stargazing.time");
		}
		return original.call(text);
	}
}
