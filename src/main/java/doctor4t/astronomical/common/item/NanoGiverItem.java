package doctor4t.astronomical.common.item;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.init.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.awt.Color;

public class NanoGiverItem extends Item {
	public NanoGiverItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack retItemStack;
//		if (user.getRandom().nextInt(2) == 0) {
		retItemStack = new ItemStack(ModItems.NANO_PLANET);
		retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("color", new Color(user.getRandom().nextFloat(), user.getRandom().nextFloat(), user.getRandom().nextFloat()).getRGB());
		retItemStack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("size", 1+user.getRandom().nextInt(10));
		user.giveItemStack(retItemStack);
//		} else {
//			retItemStack = new ItemStack(ModItems.NANO_STAR);
//
//		}

		return TypedActionResult.success(user.getStackInHand(hand));
	}
}
