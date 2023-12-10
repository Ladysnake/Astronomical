package doctor4t.astronomical.common.init;

import doctor4t.astronomical.common.Astronomical;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModSoundEvents {

	Map<SoundEvent, Identifier> SOUND_EVENTS = new LinkedHashMap<>();
	SoundEvent STAR_BREAK = createSoundEvent("entity.star.break");
	SoundEvent STAR_COLLECT = createSoundEvent("entity.star.collect");
	SoundEvent STAR_CRAFT = createSoundEvent("entity.star.craft");
	SoundEvent STAR_FALL = createSoundEvent("entity.star.fall");
	SoundEvent STAR_IMPACT = createSoundEvent("entity.star.impact");
	SoundEvent STAR_AMBIENT = createSoundEvent("entity.star.ambient");

	static void initialize() {
		SOUND_EVENTS.keySet().forEach(soundEvent -> Registry.register(Registry.SOUND_EVENT, SOUND_EVENTS.get(soundEvent), soundEvent));
	}

	private static SoundEvent createSoundEvent(String path) {
		SoundEvent soundEvent = new SoundEvent(Astronomical.id(path));
		SOUND_EVENTS.put(soundEvent, Astronomical.id(path));
		return soundEvent;
	}

}
