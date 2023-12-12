package doctor4t.astronomical.common.item;

import doctor4t.astronomical.common.Astronomical;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

import static net.minecraft.text.Style.EMPTY;

public class NanoCosmosItem extends NanoAstralObjectItem {
	public NanoCosmosItem(Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		String texture = stack.getOrCreateSubNbt(Astronomical.MOD_ID).getString("texture");
		int size = stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("size");

		tooltip.add(Text.literal("Texture: " + texture));
		tooltip.add(Text.literal("Size: " + size).setStyle(EMPTY));

		super.appendTooltip(stack, world, tooltip, context);
	}

	public enum CosmosTexture {
		VOID(Astronomical.id("textures/astral_object/cosmos/void.png")),
		STARS(Astronomical.id("textures/astral_object/cosmos/stars.png")),
		LIGHT(Astronomical.id("textures/astral_object/white.png"));


		private static final List<CosmosTexture> VALUES = List.of(values());
		public static final int SIZE = VALUES.size();
		private static final Random RANDOM = new Random();
		public final Identifier texture;

		CosmosTexture(Identifier texture) {
			this.texture = texture;
		}

		public static CosmosTexture byName(String name) {
			for (CosmosTexture ringTexture : values()) {
				if (ringTexture.name().equals(name)) {
					return ringTexture;
				}
			}
			return VOID;
		}

		public static CosmosTexture getRandom() {
			return VALUES.get(RANDOM.nextInt(SIZE));
		}
	}
}
