package doctor4t.astronomical.common.init;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.block.AstralDisplayBlock;
import doctor4t.astronomical.common.block.AstralLanternBlock;
import doctor4t.astronomical.common.block.MarshmallowCanBlock;
import doctor4t.astronomical.common.item.MarshmallowCanBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.LinkedHashMap;
import java.util.Map;

import static doctor4t.astronomical.common.init.ModItems.ITEMS;

public interface ModBlocks {
	Map<Block, Identifier> BLOCKS = new LinkedHashMap<>();

	Block ASTRAL_DISPLAY = createBlock("astral_display", new AstralDisplayBlock(QuiltBlockSettings.copy(Blocks.OBSERVER).sounds(BlockSoundGroup.COPPER)), true);
	Block MARSHMALLOW_CAN = createBlock("marshmallow_can", new MarshmallowCanBlock(QuiltBlockSettings.create().mapColor(MapColor.METAL).breakInstantly().sounds(BlockSoundGroup.LANTERN).nonOpaque().pistonBehavior(PistonBehavior.DESTROY)), true);
	Block STARMALLOW_CAN = createBlock("starmallow_can", new MarshmallowCanBlock(QuiltBlockSettings.copy(MARSHMALLOW_CAN)), true);
	Block ASTRAL_LANTERN = createBlock("astral_lantern", new AstralLanternBlock(QuiltBlockSettings.copy(Blocks.LANTERN)), true);

	static void initialize() {
		BLOCKS.keySet().forEach(block -> Registry.register(Registries.BLOCK, BLOCKS.get(block), block));
	}

	private static <T extends Block> T createBlock(String name, T block, boolean createItem) {
		BLOCKS.put(block, Astronomical.id(name));
		if (createItem) {
			var settings = new QuiltItemSettings();
			if (block instanceof MarshmallowCanBlock) {
				settings.maxCount(1);
				ITEMS.put(new MarshmallowCanBlockItem(block, settings), BLOCKS.get(block));
			} else {
				ITEMS.put(new BlockItem(block, settings), BLOCKS.get(block));
			}
		}
		return block;
	}
}
