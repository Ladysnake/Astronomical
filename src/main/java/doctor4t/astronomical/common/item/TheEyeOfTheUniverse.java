package doctor4t.astronomical.common.item;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TheEyeOfTheUniverse extends NanoAstralObjectItem {
	public TheEyeOfTheUniverse(Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack getDefaultStack() {
		ItemStack itemStack = new ItemStack(ModItems.THE_EYE_OF_THE_UNIVERSE);
		itemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("size", 1);
		return itemStack;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (!stack.getOrCreateSubNbt(Astronomical.MOD_ID).contains("size")) {
			stack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("size", 1);
		}
		super.inventoryTick(stack, world, entity, slot, selected);
	}
}
