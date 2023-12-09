package doctor4t.astronomical.common.item;

import doctor4t.astronomical.common.init.ModBlocks;
import doctor4t.astronomical.common.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public class MarshmallowCanBlockItem extends BlockItem {
	public MarshmallowCanBlockItem(Block block, Settings settings) {
		super(block, settings);
	}

	@Override
	public ItemStack getDefaultStack() {
		ItemStack ret = new ItemStack(ModBlocks.MARSHMALLOW_CAN);
		ret.getOrCreateNbt().putInt("marshmallowCount", 1);
		return ret;
	}
}
