package doctor4t.astronomical.common.item;

import doctor4t.astronomical.common.Astronomical;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

import static net.minecraft.text.Style.EMPTY;

public class NanoPlanetItem extends NanoAstralObjectItem {
	public NanoPlanetItem(Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		Color planetColor1 = new Color(stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color1"));
		Color planetColor2 = new Color(stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color2"));
		int planetSize = stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("size");

		tooltip.add(Text.literal("Base: "+String.format("#%02X%02X%02X", planetColor1.getRed(), planetColor1.getGreen(), planetColor1.getBlue())).setStyle(EMPTY.withColor(planetColor1.getRGB())));
		tooltip.add(Text.literal("Texture: "+String.format("#%02X%02X%02X", planetColor2.getRed(), planetColor2.getGreen(), planetColor2.getBlue())).setStyle(EMPTY.withColor(planetColor2.getRGB())));
		tooltip.add(Text.literal("Size: " + planetSize).setStyle(EMPTY));

		super.appendTooltip(stack, world, tooltip, context);
	}
}
