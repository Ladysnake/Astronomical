package doctor4t.astronomical.common;

import doctor4t.astronomical.common.init.*;
import doctor4t.astronomical.common.screen.AstralDisplayScreenHandler;
import doctor4t.astronomical.common.screen.PlanetColorScreenHandler;
import doctor4t.astronomical.common.screen.RingColorScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.feature_flags.FeatureFlagBitSet;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Astronomical implements ModInitializer {
	public static final Vec3d UP = new Vec3d(0, 1, 0);
	public static final String MOD_ID = "astronomical";
	public static final Color STAR_PURPLE = new Color(0xC065FF);
	public static final TagKey<Block> HEAT_SOURCES = TagKey.of(RegistryKeys.BLOCK, id("heat_sources"));
	public static final HashMap<Integer, Integer> STAR_TEMPERATURE_COLORS = new HashMap<>();
	// packets
	public static Identifier Y_LEVEL_PACKET = id("y_level");	public static final ScreenHandlerType<AstralDisplayScreenHandler> ASTRAL_DISPLAY_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER_TYPE, id("astral_display"), new ScreenHandlerType<>(AstralDisplayScreenHandler::new, FeatureFlagBitSet.empty()));
	public static Identifier ROT_SPEED_PACKET = id("rot_speed");
	public static Identifier SPIN_PACKET = id("spin");	public static final ScreenHandlerType<PlanetColorScreenHandler> PLANET_COLOR_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER_TYPE, id("planet_color"), new ScreenHandlerType<>(PlanetColorScreenHandler::new, FeatureFlagBitSet.empty()));
	public static Identifier HOLDING_PACKET = id("holding");
	public static Identifier PLANET_COLOR_CHANGE_PACKET = id("planet_color_change");	public static final ScreenHandlerType<RingColorScreenHandler> RING_COLOR_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER_TYPE, id("ring_color"), new ScreenHandlerType<>(RingColorScreenHandler::new, FeatureFlagBitSet.empty()));
	public static Identifier RING_COLOR_CHANGE_PACKET = id("ring_color_change");

	public static ItemGroup ASTRONOMICAL_ITEM_GROUP;

	static {
		STAR_TEMPERATURE_COLORS.put(10000, 0x1F8CFF);
		STAR_TEMPERATURE_COLORS.put(9000, 0x65B5FF);
		STAR_TEMPERATURE_COLORS.put(8000, 0xA6C2FF);
		STAR_TEMPERATURE_COLORS.put(7000, 0xE2E2FF);
		STAR_TEMPERATURE_COLORS.put(6000, 0xFFE0CC);
		STAR_TEMPERATURE_COLORS.put(5000, 0xFFDB87);
		STAR_TEMPERATURE_COLORS.put(4000, 0xFFBE1C);
		STAR_TEMPERATURE_COLORS.put(3000, 0xFF8800);
		STAR_TEMPERATURE_COLORS.put(2000, 0xFF5500);
		STAR_TEMPERATURE_COLORS.put(1000, 0xFF0000);
	}

	public static @NotNull Vec3d rotateViaQuat(@NotNull Vec3d rot, @NotNull Quaternionf quat) {
		float x = (float) rot.x;
		float y = (float) rot.y;
		float z = (float) rot.z;

		float ux = quat.x();
		float uy = quat.y();
		float uz = quat.z();

		float scalar = quat.w();

		float cx = -y * uz + (z * uy);
		float cy = -z * ux + (x * uz);
		float cz = -x * uy + (y * ux);

		double s1 = 2.0f * (ux * x + uy * y + uz * z);
		double s2 = scalar * scalar - (ux * ux + uy * uy + uz * uz);
		double s3 = 2.0f * scalar;

		double vpx = s1 * ux + s2 * x + s3 * cx;
		double vpy = s1 * uy + s2 * y + s3 * cy;
		double vpz = s1 * uz + s2 * z + s3 * cz;

		return new Vec3d(vpx, vpy, vpz);
	}

	public static @NotNull Quaternionf invert(@NotNull Quaternionf in) {
		float invNorm = 1.0f / Math.fma(in.x(), in.x(), Math.fma(in.y(), in.y(), Math.fma(in.z(), in.z(), in.w() * in.w())));
		return new Quaternionf(-in.x() * invNorm, -in.y() * invNorm, -in.z() * invNorm, in.w() * invNorm);
	}

	public static @NotNull Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

	public static int getStarColorForTemperature(int temperature) {
		if (STAR_TEMPERATURE_COLORS.containsKey(temperature)) {
			return STAR_TEMPERATURE_COLORS.get(temperature);
		}
		return 0;
	}

	public static int getRandomStarTemperature(RandomGenerator randomGenerator) {
		List<Integer> temperatures = new ArrayList<>(Astronomical.STAR_TEMPERATURE_COLORS.keySet());
		return temperatures.get(randomGenerator.nextInt(temperatures.size()));
	}

	public static int getRandomStarTemperature(float delta) {
		List<Integer> temperatures = new ArrayList<>(Astronomical.STAR_TEMPERATURE_COLORS.values());
		return temperatures.get(MathHelper.clamp(MathHelper.ceil(delta * temperatures.size() - 1), 0, temperatures.size()));
	}

	@Override
	public void onInitialize(ModContainer mod) {
		ModBlocks.initialize();
		ModBlockEntities.initialize();
		ModEntities.initialize();
		ModItems.initialize();
		ModSoundEvents.initialize();
		ModParticles.initialize();
		ModStatusEffects.initialize();

		ServerPlayNetworking.registerGlobalReceiver(Y_LEVEL_PACKET, (server, player, handler, buf, responseSender) -> {
			double yLevel = buf.readDouble();
			server.execute(() -> {
				ScreenHandler screenHandler = player.currentScreenHandler;
				if (screenHandler instanceof AstralDisplayScreenHandler astralDisplayScreenHandler) {
					astralDisplayScreenHandler.entity().yLevel.setValue(yLevel);

					BlockState state = astralDisplayScreenHandler.entity().getWorld().getBlockState(astralDisplayScreenHandler.entity().getPos());
					astralDisplayScreenHandler.entity().getWorld().updateListeners(astralDisplayScreenHandler.entity().getPos(), state, state, Block.NOTIFY_LISTENERS);

					astralDisplayScreenHandler.entity().markDirty();
				}
			});
		});

		ServerPlayNetworking.registerGlobalReceiver(ROT_SPEED_PACKET, (server, player, handler, buf, responseSender) -> {
			double rotSpeed = buf.readDouble();
			server.execute(() -> {
				ScreenHandler screenHandler = player.currentScreenHandler;
				if (screenHandler instanceof AstralDisplayScreenHandler astralDisplayScreenHandler) {
					astralDisplayScreenHandler.entity().rotSpeed.setValue(rotSpeed);

					BlockState state = astralDisplayScreenHandler.entity().getWorld().getBlockState(astralDisplayScreenHandler.entity().getPos());
					astralDisplayScreenHandler.entity().getWorld().updateListeners(astralDisplayScreenHandler.entity().getPos(), state, state, Block.NOTIFY_LISTENERS);

					astralDisplayScreenHandler.entity().markDirty();
				}
			});
		});

		ServerPlayNetworking.registerGlobalReceiver(SPIN_PACKET, (server, player, handler, buf, responseSender) -> {
			double spin = buf.readDouble();
			server.execute(() -> {
				ScreenHandler screenHandler = player.currentScreenHandler;
				if (screenHandler instanceof AstralDisplayScreenHandler astralDisplayScreenHandler) {
					astralDisplayScreenHandler.entity().spin.setValue(spin);

					BlockState state = astralDisplayScreenHandler.entity().getWorld().getBlockState(astralDisplayScreenHandler.entity().getPos());
					astralDisplayScreenHandler.entity().getWorld().updateListeners(astralDisplayScreenHandler.entity().getPos(), state, state, Block.NOTIFY_LISTENERS);

					astralDisplayScreenHandler.entity().markDirty();
				}
			});
		});

		ServerPlayNetworking.registerGlobalReceiver(HOLDING_PACKET, (server, player, handler, buf, responseSender) -> {
			boolean holding = buf.readBoolean();
			server.execute(() -> player.astronomical$setHoldingAttack(holding));
		});

		ServerPlayNetworking.registerGlobalReceiver(PLANET_COLOR_CHANGE_PACKET, (server, player, handler, buf, responseSender) -> {
			int color1 = buf.readInt();
			int color2 = buf.readInt();
			server.execute(() -> {
				ScreenHandler screenHandler = player.currentScreenHandler;
				if (screenHandler instanceof PlanetColorScreenHandler) {
					ItemStack stack = player.getMainHandStack();
					if (!stack.isOf(ModItems.NANO_PLANET)) {
						stack = player.getOffHandStack();
						if (!stack.isOf(ModItems.NANO_PLANET)) {
							return;
						}
					}

					stack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("color1", color1);
					stack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("color2", color2);
				}
			});
		});

		ServerPlayNetworking.registerGlobalReceiver(RING_COLOR_CHANGE_PACKET, (server, player, handler, buf, responseSender) -> {
			int color = buf.readInt();
			server.execute(() -> {
				ScreenHandler screenHandler = player.currentScreenHandler;
				if (screenHandler instanceof RingColorScreenHandler) {
					ItemStack stack = player.getMainHandStack();
					if (!stack.isOf(ModItems.NANO_RING)) {
						stack = player.getOffHandStack();
						if (!stack.isOf(ModItems.NANO_RING)) {
							return;
						}
					}

					stack.getOrCreateSubNbt(Astronomical.MOD_ID).putInt("color", color);
				}
			});
		});
	}
}
