package doctor4t.astronomical.common.init;

import doctor4t.astronomical.common.Astronomical;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

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
	SoundEvent MARSHMALLOW_CAN_STORE = createSoundEvent("block.marshmallow_can.store");
	SoundEvent MARSHMALLOW_CAN_TAKE = createSoundEvent("block.marshmallow_can.take");

	static void initialize() {
		SOUND_EVENTS.keySet().forEach(soundEvent -> Registry.register(Registries.SOUND_EVENT, SOUND_EVENTS.get(soundEvent), soundEvent));
	}

	private static SoundEvent createSoundEvent(String path) {
		SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(Astronomical.id(path));
		SOUND_EVENTS.put(soundEvent, Astronomical.id(path));
		return soundEvent;
	}

}
