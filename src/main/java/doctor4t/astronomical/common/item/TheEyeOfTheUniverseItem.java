package doctor4t.astronomical.common.item;

import doctor4t.astronomical.AstronomicalConfig;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.init.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.text.Style.EMPTY;

public class TheEyeOfTheUniverseItem extends NanoAstralObjectItem {
	public TheEyeOfTheUniverseItem(Settings settings) {
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

	@Override
	public String getTranslationKey() {
		if (AstronomicalConfig.finishedOuterWilds) {
			return super.getTranslationKey();
		} else return "[Outer Wilds Spoiler]";
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		int size = stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("size");

		tooltip.add(Text.literal("Size: " + size).setStyle(EMPTY));

		super.appendTooltip(stack, world, tooltip, context);
	}
}
