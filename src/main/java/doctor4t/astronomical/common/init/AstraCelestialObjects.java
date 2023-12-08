package doctor4t.astronomical.common.init;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.structure.CelestialObject;
import doctor4t.astronomical.common.structure.InteractableStar;
import doctor4t.astronomical.common.structure.Star;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public interface AstraCelestialObjects {
	Map<Identifier, Supplier<? extends CelestialObject>> OBJECTS = new LinkedHashMap<>();

	Supplier<Star> STAR = createStar("star", Star::new);
	Supplier<InteractableStar> SUPERNOVEABLE = createStar("supernoveable", InteractableStar::new);

	private static <T extends CelestialObject> Supplier<T> createStar(String name, Supplier<T> star) {
		OBJECTS.put(Astronomical.id(name), star);
		return star;
	}
}
