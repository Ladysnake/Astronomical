package doctor4t.astronomical.common.init;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.block.AstralDisplayBlock;
import static doctor4t.astronomical.common.init.ModItems.ITEMS;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModBlocks {
	Map<Block, Identifier> BLOCKS = new LinkedHashMap<>();

	Block ASTRAL_DISPLAY = createBlock("astral_display", new AstralDisplayBlock(QuiltBlockSettings.copy(Blocks.OBSERVER)), true, ModItems.ASTRONOMICAL_ITEM_GROUP);

	static void initialize() {
		BLOCKS.keySet().forEach(block -> Registry.register(Registry.BLOCK, BLOCKS.get(block), block));
	}

	private static <T extends Block> T createBlock(String name, T block, boolean createItem, ItemGroup itemGroup) {
		BLOCKS.put(block, Astronomical.id(name));
		if (createItem) {
			var settings = new QuiltItemSettings();
			if (itemGroup != null) {
				settings.group(itemGroup);
			}
			ITEMS.put(new BlockItem(block, settings), BLOCKS.get(block));
		}
		return block;
	}
}
