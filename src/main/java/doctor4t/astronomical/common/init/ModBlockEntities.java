package doctor4t.astronomical.common.init;

import doctor4t.astronomical.common.Astronomical;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModBlockEntities {
	Map<BlockEntityType<? extends BlockEntity>, Identifier> BLOCK_ENTITIES = new LinkedHashMap<>();

//	BlockEntityType<LiminalPortalBlockEntity> LIMINAL_PORTAL = createBlockEntity("liminal_portal", QuiltBlockEntityTypeBuilder.create(LiminalPortalBlockEntity::new, ModBlocks.LIMINAL_PORTAL).build());

	private static <T extends BlockEntityType<? extends BlockEntity>> T createBlockEntity(String name, T entity) {
		BLOCK_ENTITIES.put(entity, Astronomical.id(name));
		return entity;
	}

	static void initialize() {
		BLOCK_ENTITIES.keySet().forEach(entityType -> Registry.register(Registry.BLOCK_ENTITY_TYPE, BLOCK_ENTITIES.get(entityType), entityType));
	}


}
