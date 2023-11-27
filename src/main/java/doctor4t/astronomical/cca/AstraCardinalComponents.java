package doctor4t.astronomical.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import doctor4t.astronomical.cca.world.AstraSkyComponent;
import doctor4t.astronomical.cca.world.AstraStarfallComponent;
import doctor4t.astronomical.common.Astronomical;

public class AstraCardinalComponents implements EntityComponentInitializer {
	//WORLD
	public static final ComponentKey<AstraSkyComponent> SKY= ComponentRegistry.getOrCreate(Astronomical.id("sky"), AstraSkyComponent.class);
	public static final ComponentKey<AstraStarfallComponent> FALL = ComponentRegistry.getOrCreate(Astronomical.id("starfall"), AstraStarfallComponent.class);

	//SELF
//	public static final ComponentKey<PlayerSavedInventoryComponent> SAVED_INVENTORY = ComponentRegistry.getOrCreate(new Identifier(Astronomical.MOD_ID, "saved_inventory"), PlayerSavedInventoryComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
//		registry.registerForPlayers(SAVED_INVENTORY, PlayerSavedInventoryComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
	}
}
