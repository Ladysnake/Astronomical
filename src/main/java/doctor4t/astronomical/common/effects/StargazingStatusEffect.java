package doctor4t.astronomical.common.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.entity.effect.api.StatusEffectRemovalReason;

public class StargazingStatusEffect extends StatusEffect {
	public StargazingStatusEffect(StatusEffectType type, int color) {
		super(type, color);
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyUpdateEffect(@NotNull LivingEntity entity, int amplifier) {
		if (!entity.getWorld().isClient() && entity.getWorld().isDay()) {
			entity.removeStatusEffect(this, StatusEffectRemovalReason.EXPIRED);
		}
	}
}
