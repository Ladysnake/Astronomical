package doctor4t.astronomical.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import doctor4t.astronomical.common.Astronomical;
import net.minecraft.util.Identifier;

public class ModCardinalComponents implements EntityComponentInitializer {
//	public static final ComponentKey<PlayerSavedInventoryComponent> SAVED_INVENTORY = ComponentRegistry.getOrCreate(new Identifier(Astronomical.MOD_ID, "saved_inventory"), PlayerSavedInventoryComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
//		registry.registerForPlayers(SAVED_INVENTORY, PlayerSavedInventoryComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
	}
}
