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
	String COLOR_KEY = "color";
	String DISPLAY_KEY = "display";
	int DEFAULT_COLOR = 0xffffff;

	public NanoPlanetItem(Settings settings) {
		super(settings);
	}

	public static ItemStack blendAndSetColor(ItemStack stack, List<DyeItem> colors) {
		ItemStack itemStack = ItemStack.EMPTY;
		int[] is = new int[3];
		int i = 0;
		int j = 0;
		NanoPlanetItem flareGunItem = null;
		Item item = stack.getItem();
		if (item instanceof NanoPlanetItem) {
			flareGunItem = (NanoPlanetItem) item;
			itemStack = stack.copy();
			itemStack.setCount(1);
			if (flareGunItem.hasColor(stack)) {
				int k = flareGunItem.getColor(itemStack);
				float f = (float) (k >> 16 & 0xFF) / 255.0F;
				float g = (float) (k >> 8 & 0xFF) / 255.0F;
				float h = (float) (k & 0xFF) / 255.0F;
				i += (int) (Math.max(f, Math.max(g, h)) * 255.0F);
				is[0] += (int) (f * 255.0F);
				is[1] += (int) (g * 255.0F);
				is[2] += (int) (h * 255.0F);
				++j;
			}

			for (DyeItem dyeItem : colors) {
				int color = dyeItem.getColor().getSignColor();
				int l = (color & 0xFF0000) >> 16;
				int m = (color & 0xFF00) >> 8;
				int n = (color & 0xFF);
				float[] colorComponents = new float[]{(float) l / 255.0F, (float) m / 255.0F, (float) n / 255.0F};

				int r = (int) (colorComponents[0] * 255.0F);
				int g = (int) (colorComponents[1] * 255.0F);
				int b = (int) (colorComponents[2] * 255.0F);
				i += Math.max(r, Math.max(g, b));
				is[0] += r;
				is[1] += g;
				is[2] += b;
				++j;
			}
		}

		if (flareGunItem == null) {
			return ItemStack.EMPTY;
		} else {
			int k = is[0] / j;
			int o = is[1] / j;
			int p = is[2] / j;
			float h = (float) i / (float) j;
			float q = (float) Math.max(k, Math.max(o, p));
			k = (int) ((float) k * h / q);
			o = (int) ((float) o * h / q);
			p = (int) ((float) p * h / q);
			int var26 = (k << 8) + o;
			var26 = (var26 << 8) + p;
			flareGunItem.setColor(itemStack, var26);
			return itemStack;
		}
	}

	public boolean hasColor(ItemStack stack) {
		NbtCompound nbtCompound = stack.getSubNbt(DISPLAY_KEY);
		return nbtCompound != null && nbtCompound.contains(COLOR_KEY, NbtElement.NUMBER_TYPE);
	}

	public int getColor(ItemStack stack) {
		NbtCompound nbtCompound = stack.getSubNbt(DISPLAY_KEY);
		return nbtCompound != null && nbtCompound.contains(COLOR_KEY, NbtElement.NUMBER_TYPE) ? nbtCompound.getInt(COLOR_KEY) : DEFAULT_COLOR;
	}

	public void setColor(ItemStack stack, int color) {
		stack.getOrCreateSubNbt(DISPLAY_KEY).putInt(COLOR_KEY, color);
	}

	public void removeColor(ItemStack stack) {
		NbtCompound nbtCompound = stack.getSubNbt(DISPLAY_KEY);
		if (nbtCompound != null && nbtCompound.contains(COLOR_KEY)) {
			nbtCompound.remove(COLOR_KEY);
		}

	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		int planetColor = stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color");
		int planetSize = stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("size");

		tooltip.add(Text.literal("0x" + planetColor).setStyle(EMPTY.withColor(planetColor)));
		tooltip.add(Text.literal("Size: " + planetSize).setStyle(EMPTY));

		super.appendTooltip(stack, world, tooltip, context);
	}
}
