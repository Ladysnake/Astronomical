package doctor4t.astronomical.common.init;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.block.entity.AstralDisplayBlockEntity;
import doctor4t.astronomical.common.block.entity.AstralLanternBlockEntity;
import doctor4t.astronomical.common.block.entity.MarshmallowCanBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModBlockEntities {
	Map<BlockEntityType<? extends BlockEntity>, Identifier> BLOCK_ENTITIES = new LinkedHashMap<>();

	private static <T extends BlockEntityType<? extends BlockEntity>> T createBlockEntity(String name, T entity) {
		BLOCK_ENTITIES.put(entity, Astronomical.id(name));
		return entity;
	}

	static void initialize() {
		BLOCK_ENTITIES.keySet().forEach(entityType -> Registry.register(Registries.BLOCK_ENTITY_TYPE, BLOCK_ENTITIES.get(entityType), entityType));
	}	BlockEntityType<AstralDisplayBlockEntity> ASTRAL_DISPLAY = createBlockEntity("astral_display", QuiltBlockEntityTypeBuilder.create(AstralDisplayBlockEntity::new, ModBlocks.ASTRAL_DISPLAY).build());
	BlockEntityType<MarshmallowCanBlockEntity> MARSHMALLOW_CAN = createBlockEntity("marshmallow_can", QuiltBlockEntityTypeBuilder.create(MarshmallowCanBlockEntity::new, ModBlocks.MARSHMALLOW_CAN).build());
	BlockEntityType<AstralLanternBlockEntity> ASTRAL_LANTERN = createBlockEntity("astral_lantern", QuiltBlockEntityTypeBuilder.create(AstralLanternBlockEntity::new, ModBlocks.ASTRAL_LANTERN).build());




}
