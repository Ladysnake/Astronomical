package doctor4t.astronomical.common.init;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.effects.StargazingStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModStatusEffects {
	Map<StatusEffect, Identifier> STATUS_EFFECTS = new LinkedHashMap<>();

	StatusEffect STARGAZING = createStatusEffect("stargazing", new StargazingStatusEffect(StatusEffectType.BENEFICIAL, 0x6300E5));
	StatusEffect STARFALL = createStatusEffect("starfall", new StargazingStatusEffect(StatusEffectType.BENEFICIAL, 0x6300E5));


	private static <T extends StatusEffect> T createStatusEffect(String name, T statusEffect) {
		STATUS_EFFECTS.put(statusEffect, Astronomical.id(name));
		return statusEffect;
	}

	static void initialize() {
		STATUS_EFFECTS.keySet().forEach(item -> Registry.register(Registry.STATUS_EFFECT, STATUS_EFFECTS.get(item), item));
	}
}
