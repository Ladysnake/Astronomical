package doctor4t.astronomical.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import doctor4t.astronomical.cca.entity.HoldingComponent;
import doctor4t.astronomical.cca.world.AstraSkyComponent;
import doctor4t.astronomical.cca.world.AstraStarfallComponent;
import doctor4t.astronomical.common.Astronomical;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public class AstraCardinalComponents implements EntityComponentInitializer, WorldComponentInitializer {
	//WORLD
	public static final ComponentKey<AstraSkyComponent> SKY = ComponentRegistry.getOrCreate(Astronomical.id("sky"), AstraSkyComponent.class);
	public static final ComponentKey<AstraStarfallComponent> FALL = ComponentRegistry.getOrCreate(Astronomical.id("starfall"), AstraStarfallComponent.class);

	//SELF
	public static final ComponentKey<HoldingComponent> HOLDING = ComponentRegistry.getOrCreate(Astronomical.HOLDING_PACKET, HoldingComponent.class);

	@Override
	public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
		registry.beginRegistration(PlayerEntity.class, HOLDING).respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end(HoldingComponent::new);
	}

	@Override
	public void registerWorldComponentFactories(@NotNull WorldComponentFactoryRegistry registry) {
		registry.register(AstraCardinalComponents.SKY, AstraSkyComponent::new);
		registry.register(AstraCardinalComponents.FALL, AstraStarfallComponent::new);
	}
}
