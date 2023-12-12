package doctor4t.astronomical.common.item;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.screen.PlanetColorScreenHandler;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
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
		Color color1 = new Color(stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color1"));
		Color color2 = new Color(stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color2"));
		String texture = stack.getOrCreateSubNbt(Astronomical.MOD_ID).getString("texture");
		int size = stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("size");

		tooltip.add(Text.literal("Texture: " + texture));
		tooltip.add(Text.literal("Colors: ").append(Text.literal(String.format("#%02X%02X%02X", color1.getRed(), color1.getGreen(), color1.getBlue())).setStyle(EMPTY.withColor(color1.getRGB()))).append(Text.literal(" " + String.format("#%02X%02X%02X", color2.getRed(), color2.getGreen(), color2.getBlue())).setStyle(EMPTY.withColor(color2.getRGB()))));
		tooltip.add(Text.literal("Size: " + size).setStyle(EMPTY));

		super.appendTooltip(stack, world, tooltip, context);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		user.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerx) -> new PlanetColorScreenHandler(syncId, playerInventory), Text.literal("")));

		return super.use(world, user, hand);
	}

	public enum PlanetTexture {
		ACID(Astronomical.id("textures/astral_object/planet/acid.png")),
		ATMOSPHERE(Astronomical.id("textures/astral_object/planet/atmosphere.png")),
		CLOUDS(Astronomical.id("textures/astral_object/planet/clouds.png")),
		CRATERS(Astronomical.id("textures/astral_object/planet/craters.png")),
		HAZE(Astronomical.id("textures/astral_object/planet/haze.png")),
		HOME(Astronomical.id("textures/astral_object/planet/home.png")),
		ICE(Astronomical.id("textures/astral_object/planet/ice.png")),
		ROCK(Astronomical.id("textures/astral_object/planet/rock.png")),
		STRIPES(Astronomical.id("textures/astral_object/planet/stripes.png")),
		SWIRL(Astronomical.id("textures/astral_object/planet/swirl.png"));


		private static final List<PlanetTexture> VALUES = List.of(values());
		public static final int SIZE = VALUES.size();
		private static final Random RANDOM = new Random();
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

		public static PlanetTexture getRandom() {
			return VALUES.get(RANDOM.nextInt(SIZE));
		}
	}
}
