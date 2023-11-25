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

public class NanoPlanetItem extends Item {
	public NanoPlanetItem(Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		int planetColor1 = stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color1");
		int planetColor2 = stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color2");
		int planetSize = stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("size");

		tooltip.add(Text.literal("0x" + planetColor1).setStyle(EMPTY.withColor(planetColor1)));
		tooltip.add(Text.literal("0x" + planetColor2).setStyle(EMPTY.withColor(planetColor2)));
		tooltip.add(Text.literal("Size: " + planetSize).setStyle(EMPTY));

		super.appendTooltip(stack, world, tooltip, context);
	}
}
