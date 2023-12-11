package doctor4t.astronomical.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public class MarshmallowCanBlockItem extends BlockItem {
	public MarshmallowCanBlockItem(Block block, Settings settings) {
		super(block, settings);
	}

	@Override
	public ItemStack getDefaultStack() {
		ItemStack ret = new ItemStack(this.getBlock());
		ret.getOrCreateNbt().putInt("marshmallowCount", 1);
		return ret;
	}
}
