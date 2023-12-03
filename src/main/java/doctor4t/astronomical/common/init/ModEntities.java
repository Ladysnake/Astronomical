package doctor4t.astronomical.common.init;

import doctor4t.astronomical.common.Astronomical;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModEntities {

	Map<EntityType<? extends Entity>, Identifier> ENTITIES = new LinkedHashMap<>();
//	EntityType<StarEntity> STAR = createEntity("star", QuiltEntityTypeBuilder.<StarEntity>create(SpawnGroup.MISC, StarEntity::new).setDimensions(EntityDimensions.fixed(0.2f, 0.2f)).build());

	private static <T extends EntityType<? extends Entity>> T createEntity(String name, T entity) {
		ENTITIES.put(entity, Astronomical.id(name));
		return entity;
	}

	static void initialize() {
		ENTITIES.keySet().forEach(entityType -> Registry.register(Registry.ENTITY_TYPE, ENTITIES.get(entityType), entityType));
	}

}
