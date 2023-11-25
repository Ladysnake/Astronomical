package doctor4t.astronomical.common.item;

import doctor4t.astronomical.common.Astronomical;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.amymialee.mialeemisc.util.MialeeText;

import java.util.List;

import static net.minecraft.text.Style.EMPTY;

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
