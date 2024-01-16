package doctor4t.astronomical.common.item;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.screen.RingColorScreenHandler;
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

public class NanoRingItem extends NanoAstralObjectItem {
	public NanoRingItem(Settings settings) {
		super(settings);
	}


	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		Color color = new Color(stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("color"));
		String texture = stack.getOrCreateSubNbt(Astronomical.MOD_ID).getString("texture");
		int size = stack.getOrCreateSubNbt(Astronomical.MOD_ID).getInt("size");

		tooltip.add(Text.literal("Texture: " + texture));
		tooltip.add(Text.literal("Color: ").append(Text.literal(String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue())).setStyle(EMPTY.withColor(color.getRGB()))));
		tooltip.add(Text.literal("Size: " + size).setStyle(EMPTY));

		super.appendTooltip(stack, world, tooltip, context);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		user.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerx) -> new RingColorScreenHandler(syncId, playerInventory), Text.literal("")));

		return super.use(world, user, hand);
	}

	public enum RingTexture {
		PARTICLES(Astronomical.id("textures/astral_object/ring/particles.png")),
		ASTEROIDS(Astronomical.id("textures/astral_object/ring/asteroids.png")),
		ORGANIC(Astronomical.id("textures/astral_object/ring/organic.png")),
		HALO(Astronomical.id("textures/astral_object/ring/halo.png")),
		DUST(Astronomical.id("textures/astral_object/ring/dust.png"));


		private static final List<RingTexture> VALUES = List.of(values());
		public static final int SIZE = VALUES.size();
		private static final Random RANDOM = new Random();
		public final Identifier texture;

		RingTexture(Identifier texture) {
			this.texture = texture;
		}

		public static RingTexture byName(String name) {
			for (RingTexture ringTexture : values()) {
				if (ringTexture.name().equals(name)) {
					return ringTexture;
				}
			}
			return PARTICLES;
		}

		public static RingTexture getRandom() {
			return VALUES.get(RANDOM.nextInt(SIZE));
		}
	}
}
