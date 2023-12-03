package doctor4t.astronomical.common.item;

import doctor4t.astronomical.common.Astronomical;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.Random;

import static net.minecraft.text.Style.EMPTY;

public class NanoPlanetItem extends NanoAstralObjectItem {
	public NanoPlanetItem(Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		Color planetColor1 = new Color(stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color1"));
		Color planetColor2 = new Color(stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color2"));
		String planetTexture = stack.getOrCreateSubNbt(Astronomical.MOD_ID).getString("texture");
		int planetSize = stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("size");

		tooltip.add(Text.literal("Base: " + String.format("#%02X%02X%02X", planetColor1.getRed(), planetColor1.getGreen(), planetColor1.getBlue())).setStyle(EMPTY.withColor(planetColor1.getRGB())));
		tooltip.add(Text.literal("PlanetTexture: " + planetTexture + " " + String.format("#%02X%02X%02X", planetColor2.getRed(), planetColor2.getGreen(), planetColor2.getBlue())).setStyle(EMPTY.withColor(planetColor2.getRGB())));
		tooltip.add(Text.literal("Size: " + planetSize).setStyle(EMPTY));

		super.appendTooltip(stack, world, tooltip, context);
	}


	public enum PlanetTexture {
		ACID(Astronomical.id("textures/astral_object/acid.png")),
		CLOUDS(Astronomical.id("textures/astral_object/clouds.png")),
		CRATERS(Astronomical.id("textures/astral_object/craters.png")),
		HOME(Astronomical.id("textures/astral_object/home.png")),
		ICE(Astronomical.id("textures/astral_object/ice.png")),
		ROCK(Astronomical.id("textures/astral_object/rock.png")),
		RUST(Astronomical.id("textures/astral_object/rust.png")),
		STRIPES(Astronomical.id("textures/astral_object/stripes.png")),
		SULFUR(Astronomical.id("textures/astral_object/sulfur.png")),
		SWIRL(Astronomical.id("textures/astral_object/swirl.png"));


		public final Identifier texture;

		PlanetTexture(Identifier texture) {
			this.texture = texture;
		}

		public static PlanetTexture byName(String name) {
			for (PlanetTexture planetTexture : values()) {
				if (planetTexture.name().equals(name)) {
					return planetTexture;
				}
			}
			return HOME;
		}

		private static final List<PlanetTexture> VALUES = List.of(values());
		private static final int SIZE = VALUES.size();
		private static final Random RANDOM = new Random();

		public static PlanetTexture getRandom()  {
			return VALUES.get(RANDOM.nextInt(SIZE));
		}
	}
}
