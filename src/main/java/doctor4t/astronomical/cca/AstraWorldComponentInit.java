package doctor4t.astronomical.cca;

import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import doctor4t.astronomical.cca.world.AstraSkyComponent;
import doctor4t.astronomical.cca.world.AstraStarfallComponent;

public class AstraWorldComponentInit implements WorldComponentInitializer {
	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
		registry.register(AstraCardinalComponents.SKY, AstraSkyComponent::new);
		registry.register(AstraCardinalComponents.FALL, AstraStarfallComponent::new);
	}
}
