package doctor4t.astronomical.common.init;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.entity.FallenStarEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModEntities {

	Map<EntityType<? extends Entity>, Identifier> ENTITIES = new LinkedHashMap<>();
	EntityType<FallenStarEntity> FALLEN_STAR = createEntity("fallen_star", QuiltEntityTypeBuilder.<FallenStarEntity>create(SpawnGroup.MISC, FallenStarEntity::new).setDimensions(EntityDimensions.fixed(0.5f, 0.5f)).maxChunkTrackingRange(16).build());

	private static <T extends EntityType<? extends Entity>> T createEntity(String name, T entity) {
		ENTITIES.put(entity, Astronomical.id(name));
		return entity;
	}

	static void initialize() {
		ENTITIES.keySet().forEach(entityType -> Registry.register(Registries.ENTITY_TYPE, ENTITIES.get(entityType), entityType));
	}

}
