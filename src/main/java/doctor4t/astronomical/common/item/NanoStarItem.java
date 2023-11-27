package doctor4t.astronomical.common.item;

import doctor4t.astronomical.common.Astronomical;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NanoStarItem extends NanoAstralObjectItem {
	public NanoStarItem(Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(@NotNull ItemStack stack, @Nullable World world, @NotNull List<Text> tooltip, TooltipContext context) {
		var temperature = stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("temperature");
		var size = stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("size");
		tooltip.add(Text.literal("Temperature: " + temperature + " K").setStyle(Style.EMPTY.withColor(Astronomical.getStarColorForTemperature(temperature))));
		tooltip.add(Text.literal("Size: " + size).setStyle(Style.EMPTY));
		super.appendTooltip(stack, world, tooltip, context);
	}
}
