package doctor4t.astronomical.common.item;

import doctor4t.astronomical.common.Astronomical;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import static net.minecraft.text.Style.EMPTY;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NanoStarItem extends Item {
	public NanoStarItem(Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		int starColor = stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color");
		int starSize = stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("size");

		tooltip.add(Text.literal("0x" + starColor).setStyle(EMPTY.withColor(starColor)));
		tooltip.add(Text.literal("Size: " + starSize).setStyle(EMPTY));

		super.appendTooltip(stack, world, tooltip, context);
	}
}
